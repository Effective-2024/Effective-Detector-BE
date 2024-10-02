package com.effective.detector.common.config

import com.effective.detector.common.handler.WebSocketImageStreamHandler
import com.effective.detector.hospital.domain.CameraRepository
import com.effective.detector.image.application.ImageService
import com.google.gson.Gson
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry


@EnableWebSocket
@Configuration
class WebSocketConfig(
    private val rabbitTemplate: RabbitTemplate,
    private val imageService: ImageService,
    private val messagingTemplate: SimpMessagingTemplate,
    private val gson: Gson,
    private val cameraRepository: CameraRepository,
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(
            WebSocketImageStreamHandler(
                rabbitTemplate, imageService, messagingTemplate, gson, cameraRepository
            ),
            "/image-stream"
        ).setAllowedOrigins("*")
    }
}
