package com.effective.detector.common.handler

import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

class WebSocketAccidentDetectHandler : TextWebSocketHandler() {

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            val payload = message.payload
            println("Received message: $payload")

            // TODO: RabbitMQ에 사고 발생 메세지 브로킹.
            // TODO: 사고 발생 영상을 문자로 전송.
            session.sendMessage(TextMessage("Accident Received Successfully."))
        } catch (e: Exception) {
            e.printStackTrace()
            session.sendMessage(TextMessage("""{"status": "error", "message": "Invalid JSON"}"""))
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        println("WebSocket Accident Connection Established.")
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        println("WebSocket Accident Connection Closed.")
    }
}
