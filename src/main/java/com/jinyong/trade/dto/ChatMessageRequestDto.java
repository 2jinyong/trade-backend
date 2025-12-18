package com.jinyong.trade.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageRequestDto {

    private Long roomId;      // 채팅방 ID
    private String content;   // 메시지 내용
}
