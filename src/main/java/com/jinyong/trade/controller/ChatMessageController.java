package com.jinyong.trade.controller;

import com.jinyong.trade.dto.ChatMessageRequestDto;
import com.jinyong.trade.dto.ChatMessageResponseDto;
import com.jinyong.trade.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // 클라이언트가 /pub/chat/message 로 메시지를 보내면 이 메서드가 호출됨
    @MessageMapping("/chat/message")
    public void sendMessage(
            ChatMessageRequestDto request,
            @Header("senderUserId") String senderUserId  // 헤더에서 보낸 사람 정보
    ) {
        // 메시지 저장
        ChatMessageResponseDto response = chatService.saveMessage(
                request.getRoomId(),
                senderUserId,
                request.getContent()
        );

        // 해당 채팅방을 구독 중인 클라이언트들에게 메시지 전송
        // /sub/chat/room/{roomId} 를 구독 중인 사람들에게 전달
        messagingTemplate.convertAndSend(
                "/sub/chat/room/" + request.getRoomId(),
                response
        );
    }
}
