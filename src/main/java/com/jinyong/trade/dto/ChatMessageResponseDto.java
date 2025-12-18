package com.jinyong.trade.dto;

import com.jinyong.trade.entity.ChatMessage;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatMessageResponseDto {

    private Long messageId;
    private Long roomId;
    private String senderUserId;
    private String senderName;
    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;

    public ChatMessageResponseDto(ChatMessage message) {
        this.messageId = message.getId();
        this.roomId = message.getChatRoom().getId();
        this.senderUserId = message.getSender().getUserId();
        this.senderName = message.getSender().getName();
        this.content = message.getContent();
        this.isRead = message.isRead();
        this.createdAt = message.getCreatedAt();
    }
}
