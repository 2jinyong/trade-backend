package com.jinyong.trade.dto;

import com.jinyong.trade.entity.ChatRoom;
import com.jinyong.trade.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatRoomResponseDto {

    private Long roomId;
    private String partnerUserId;  // 상대방 userId
    private String partnerName;    // 상대방 이름
    private LocalDateTime createdAt;

    // 현재 로그인한 사용자 기준으로 상대방 정보 추출
    public ChatRoomResponseDto(ChatRoom chatRoom, User currentUser) {
        this.roomId = chatRoom.getId();
        this.createdAt = chatRoom.getCreatedAt();

        // 내가 sender면 상대방은 receiver, 내가 receiver면 상대방은 sender
        User partner = chatRoom.getSender().getUserId().equals(currentUser.getUserId())
                ? chatRoom.getReceiver()
                : chatRoom.getSender();

        this.partnerUserId = partner.getUserId();
        this.partnerName = partner.getName();
    }
}
