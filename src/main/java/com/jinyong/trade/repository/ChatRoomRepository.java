package com.jinyong.trade.repository;

import com.jinyong.trade.entity.ChatRoom;
import com.jinyong.trade.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 두 사용자 간의 채팅방 찾기 (순서 무관)
    // A가 B에게 보냈든, B가 A에게 보냈든 같은 채팅방
    @Query("SELECT c FROM ChatRoom c WHERE " +
           "(c.sender = :user1 AND c.receiver = :user2) OR " +
           "(c.sender = :user2 AND c.receiver = :user1)")
    Optional<ChatRoom> findByUsers(@Param("user1") User user1, @Param("user2") User user2);

    // 내가 참여한 모든 채팅방 목록
    @Query("SELECT c FROM ChatRoom c WHERE c.sender = :user OR c.receiver = :user ORDER BY c.updatedAt DESC")
    List<ChatRoom> findByUser(@Param("user") User user);
}
