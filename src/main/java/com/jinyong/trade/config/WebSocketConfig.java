package com.jinyong.trade.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // WebSocket 메시지 브로커 활성화
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 메시지 브로커 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트가 구독할 경로 prefix
        // 예: /sub/chat/room/123 구독 -> 해당 방의 메시지 수신
        registry.enableSimpleBroker("/sub");

        // 클라이언트가 메시지 보낼 경로 prefix
        // 예: /pub/chat/message -> 서버의 @MessageMapping 메서드로 전달
        registry.setApplicationDestinationPrefixes("/pub");
    }

    // WebSocket 연결 엔드포인트 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 WebSocket 연결할 주소
        // ws://localhost:8081/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // CORS 허용
                .withSockJS(); // SockJS 지원 (WebSocket 미지원 브라우저 대응)
    }
}
