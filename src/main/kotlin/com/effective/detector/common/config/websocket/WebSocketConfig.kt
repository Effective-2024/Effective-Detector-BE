package com.effective.detector.common.config.websocket

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/topic") // 클라이언트가 구독할 수 있는 목적지(prefix)를 설정합니다.
        config.setApplicationDestinationPrefixes("/app") // 메시지를 보낼 때 사용하는 prefix를 설정합니다.
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/image-stream")
            .setAllowedOriginPatterns("http://localhost:3000")
            .withSockJS() // STOMP 엔드포인트를 설정하고, SockJS 지원을 추가합니다.
    }
}
