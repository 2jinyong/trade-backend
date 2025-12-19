package com.jinyong.trade.service;

import com.jinyong.trade.dto.ChatMessageResponseDto;
import com.jinyong.trade.dto.ChatRoomResponseDto;
import com.jinyong.trade.entity.ChatMessage;
import com.jinyong.trade.entity.ChatRoom;
import com.jinyong.trade.entity.User;
import com.jinyong.trade.repository.ChatMessageRepository;
import com.jinyong.trade.repository.ChatRoomRepository;
import com.jinyong.trade.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    // 채팅방 생성 또는 기존 채팅방 반환
    @Transactional
    public ChatRoom createOrGetRoom(String myUserId, String partnerUserId) {

        User me = userRepository.findByUserId(myUserId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        User partner = userRepository.findByUserId(partnerUserId)
                .orElseThrow(() -> new RuntimeException("상대방 없음"));

        // 내가 나가지 않은 채팅방 중에서 찾기
        return chatRoomRepository.findByUsers(me, partner)
                .filter(room -> {
                    // 내가 이미 나간 채팅방이면 제외
                    if (room.getSender().equals(me) && room.isSenderLeft()) {
                        return false;
                    } else if (room.getReceiver().equals(me) && room.isReceiverLeft()) {
                        return false;
                    }
                    return true;
                })
                .orElseGet(() -> {
                    // 없으면 새로 생성
                    ChatRoom newRoom = new ChatRoom(me, partner);
                    return chatRoomRepository.save(newRoom);
                });
    }

    // 내 채팅방 목록 조회 (나간 채팅방 제외)
    public List<ChatRoomResponseDto> getMyChatRooms(String userId) {

        User me = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        return chatRoomRepository.findByUser(me)
                .stream()
                .filter(room -> {
                    // 내가 sender면 senderLeft 확인, receiver면 receiverLeft 확인
                    if (room.getSender().equals(me)) {
                        return !room.isSenderLeft();
                    } else {
                        return !room.isReceiverLeft();
                    }
                })
                .map(room -> new ChatRoomResponseDto(room, me))
                .toList();
    }

    // 채팅방 나가기
    @Transactional
    public void leaveRoom(Long roomId, String userId) {
        User me = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방 없음"));

        // 내가 sender인지 receiver인지 확인 후 나가기 처리
        if (room.getSender().equals(me)) {
            room.setSenderLeft(true);
        } else if (room.getReceiver().equals(me)) {
            room.setReceiverLeft(true);
        } else {
            throw new RuntimeException("채팅방 참여자가 아닙니다.");
        }
    }

    // 채팅방의 메시지 목록 조회
    public List<ChatMessageResponseDto> getMessages(Long roomId) {

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방 없음"));

        return chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(room)
                .stream()
                .map(ChatMessageResponseDto::new)
                .toList();
    }

    // 메시지 저장 (WebSocket에서 호출)
    @Transactional
    public ChatMessageResponseDto saveMessage(Long roomId, String senderUserId, String content) {

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방 없음"));

        User sender = userRepository.findByUserId(senderUserId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // 새 메시지가 오면 나간 상대방 복귀 처리
        if (room.getSender().equals(sender)) {
            // 내가 sender면 receiver가 나갔었는지 확인
            if (room.isReceiverLeft()) {
                room.setReceiverLeft(false);
            }
        } else {
            // 내가 receiver면 sender가 나갔었는지 확인
            if (room.isSenderLeft()) {
                room.setSenderLeft(false);
            }
        }

        ChatMessage message = new ChatMessage(room, sender, content);
        chatMessageRepository.save(message);

        return new ChatMessageResponseDto(message);
    }
}
