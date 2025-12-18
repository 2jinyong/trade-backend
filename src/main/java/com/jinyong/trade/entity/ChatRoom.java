package com.jinyong.trade.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 채팅방 참여자 1 (채팅 요청한 사람)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    // 채팅방 참여자 2 (채팅 받는 사람)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    // 나가기 여부
    @Column(nullable = false)
    private boolean senderLeft = false;

    @Column(nullable = false)
    private boolean receiverLeft = false;

    // 1대1 채팅방 생성
    public ChatRoom(User sender, User receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }
}
