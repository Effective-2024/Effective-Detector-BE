package com.effective.detector.common.handler

import org.springframework.web.socket.BinaryMessage
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.BinaryWebSocketHandler
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.ByteBuffer

class WebSocketImageStreamHandler : BinaryWebSocketHandler() {

    override fun handleBinaryMessage(session: WebSocketSession, message: BinaryMessage) {
        val payload: ByteBuffer = message.payload
        val inputStream: InputStream = ByteArrayInputStream(payload.array())

        // TODO: Redis에 파일들 저장
        //val outputPath: Path = Paths.get(UUID.randomUUID().toString() + ".png")
        //Files.copy(inputStream, outputPath, StandardCopyOption.REPLACE_EXISTING)
        session.sendMessage(TextMessage("Image Received Successfully."))
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        println("WebSocket Image Stream Connection Established.")
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: org.springframework.web.socket.CloseStatus) {
        println("WebSocket Image Stream Connection Closed.")
    }
}
