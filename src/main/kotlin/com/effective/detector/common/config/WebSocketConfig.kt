package com.effective.detector.common.config

import com.effective.detector.common.handler.WebSocketAccidentDetectHandler
import com.effective.detector.common.handler.WebSocketImageStreamHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry


@EnableWebSocket
@Configuration
class WebSocketConfig : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(WebSocketImageStreamHandler(), "/image-stream").setAllowedOrigins("*")
        registry.addHandler(WebSocketAccidentDetectHandler(), "/accident-detect").setAllowedOrigins("*")
    }
}
