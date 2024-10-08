package com.effective.detector.common.handler

import com.effective.detector.common.util.logInfo
import com.effective.detector.hospital.application.AccidentService
import com.effective.detector.image.application.ImageService
import com.google.gson.Gson
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.BinaryWebSocketHandler
import java.io.IOException
import java.util.Base64

class WebSocketImageStreamHandler(
    private val rabbitTemplate: RabbitTemplate,
    private val imageService: ImageService,
    private val accidentService: AccidentService,
    private val gson: Gson,
) : BinaryWebSocketHandler() {

    companion object {
        const val QUEUE_NAME_PREFIX = "image-queue"
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            logInfo("Received message")
            val accidentDto = gson.fromJson(message.payload, AccidentDto::class.java)
            val queueName = "$QUEUE_NAME_PREFIX-${accidentDto.hospitalId}-${accidentDto.cameraId}"
            existQueueOrExecute(queueName)
            if (accidentDto.isAccident) {
                accidentService.accident(queueName, accidentDto.hospitalId, accidentDto.cameraId)
                session.sendMessage(TextMessage("Accident Received Successfully."))
                return
            }
            val decodedBytes: ByteArray = Base64.getDecoder().decode(accidentDto.encodedImage)
            imageService.sendMessage(decodedBytes, queueName, accidentDto.hospitalId, accidentDto.cameraId)
            session.sendMessage(TextMessage("Image Received Successfully."))
        } catch (e: Exception) {
            e.printStackTrace()
            session.sendMessage(TextMessage("""{"status": "error", "message": "Invalid JSON"}"""))
        }
    }

    private fun existQueueOrExecute(queueName: String) {
        rabbitTemplate.execute { channel ->
            try {
                channel.queueDeclarePassive(queueName) // 큐가 존재하는지 확인 (큐가 없으면 IOException 발생)
                logInfo("Queue '$queueName' already exists.")
            } catch (e: IOException) { // 큐가 없을 때 예외가 발생하므로 큐를 선언
                logInfo("Queue '$queueName' does not exist. Declaring the queue.")
                channel.queueDeclare(queueName, false, true, true, null)
            }
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        logInfo("WebSocket Image Stream Connection Established.")
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: org.springframework.web.socket.CloseStatus) {
        logInfo("WebSocket Image Stream Connection Closed.")
    }
}

data class AccidentDto(
    val encodedImage: String?,
    val hospitalId: Long,
    val cameraId: Long,
    val isAccident: Boolean,
)

data class ImageMessageDto(
    val encodedImage: String,
)
