package com.effective.detector.common.handler

import com.effective.detector.common.util.findByIdOrThrow
import com.effective.detector.common.util.logInfo
import com.effective.detector.hospital.domain.CameraRepository
import com.effective.detector.image.application.ImageService
import com.google.gson.Gson
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.socket.BinaryMessage
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.BinaryWebSocketHandler
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.ByteBuffer

class WebSocketImageStreamHandler(
    private val rabbitTemplate: RabbitTemplate,
    private val imageService: ImageService,
    private val messagingTemplate: SimpMessagingTemplate,
    private val gson: Gson,
    private val cameraRepository: CameraRepository,
) : BinaryWebSocketHandler() {

    companion object {
        const val QUEUE_NAME_PREFIX = "image-queue"
    }

    private val sessionQueueMap = mutableMapOf<String, String>() // 세션 ID와 큐 이름 매핑
    private val cameraIdMap = mutableMapOf<String, Long>() // 세션 ID 와 카메라 ID 매핑

    override fun handleBinaryMessage(session: WebSocketSession, message: BinaryMessage) {
        val payload: ByteBuffer = message.payload
        val inputStream: InputStream = ByteArrayInputStream(payload.array())

        val queueName = sessionQueueMap[session.id]
        imageService.sendMessage(inputStream.readBytes(), queueName!!)
        cameraIdMap[session.id]?.let { cameraId ->
            sendClientImage(cameraId, inputStream.readBytes())
        }
        session.sendMessage(TextMessage("Image Received Successfully."))
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            logInfo("Received message: ${message.payload}")
            val accidentDto = gson.fromJson(message.payload, AccidentDto::class.java)

            if (!accidentDto.isAccident) {
                cameraIdMap[session.id] = accidentDto.cameraId
                session.sendMessage(TextMessage("Id Setting Successfully."))
                return
            }
            val queueName = sessionQueueMap[session.id]
            imageService.accident(queueName!!, accidentDto.hospitalId, accidentDto.cameraId)
            session.sendMessage(TextMessage("Accident Received Successfully."))
        } catch (e: Exception) {
            e.printStackTrace()
            session.sendMessage(TextMessage("""{"status": "error", "message": "Invalid JSON"}"""))
        }
    }

    private fun sendClientImage(cameraId: Long, readBytes: ByteArray) {
        val camera = cameraRepository.findByIdOrThrow(cameraId)
        val topicPath = "/ws/topic/image/hospitals/${camera.hospital.id}/cameras/$cameraId"
        logInfo("Send WebSocket STOMP Message To Path: $topicPath")
        messagingTemplate.convertAndSend(
            topicPath,
            ImageMessageDto(
                readBytes
            )
        )
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val queueName = "$QUEUE_NAME_PREFIX-${session.id}"
        rabbitTemplate.execute {
            it.queueDeclare(queueName, false, true, true, null)
        }
        sessionQueueMap[session.id] = queueName
        logInfo("WebSocket Image Stream Connection Established.")
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: org.springframework.web.socket.CloseStatus) {
        val queueName = sessionQueueMap.remove(session.id)
        if (queueName != null) {
            rabbitTemplate.execute {
                it.queueDelete(queueName)
            }
        }
        logInfo("WebSocket Image Stream Connection Closed.")
    }
}

data class AccidentDto(
    val hospitalId: Long,
    val cameraId: Long,
    val isAccident: Boolean,
)

data class ImageMessageDto(
    val bytes: ByteArray,
)
