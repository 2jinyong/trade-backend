package com.jinyong.trade.repository;

import com.jinyong.trade.entity.ChatMessage;
import com.jinyong.trade.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // 특정 채팅방의 메시지 목록 (오래된 순)
    List<ChatMessage> findByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);

    // 특정 채팅방의 안 읽은 메시지 개수
    int countByChatRoomAndIsReadFalseAndSenderNot(ChatRoom chatRoom, com.jinyong.trade.entity.User user);
}
