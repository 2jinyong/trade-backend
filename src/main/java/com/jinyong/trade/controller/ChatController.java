package com.jinyong.trade.controller;

import com.jinyong.trade.dto.ChatMessageResponseDto;
import com.jinyong.trade.dto.ChatRoomResponseDto;
import com.jinyong.trade.entity.ChatRoom;
import com.jinyong.trade.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 채팅방 생성 (또는 기존 채팅방 반환)
    // POST /api/chat/room
    // Body: { "partnerUserId": "상대방ID" }
    @PostMapping("/room")
    public ResponseEntity<Map<String, Long>> createRoom(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails user
    ) {
        String partnerUserId = body.get("partnerUserId");
        ChatRoom room = chatService.createOrGetRoom(user.getUsername(), partnerUserId);

        return ResponseEntity.ok(Map.of("roomId", room.getId()));
    }

    // 내 채팅방 목록 조회
    // GET /api/chat/rooms
    @GetMapping("/rooms")
    public List<ChatRoomResponseDto> getMyRooms(
            @AuthenticationPrincipal UserDetails user
    ) {
        return chatService.getMyChatRooms(user.getUsername());
    }

    // 특정 채팅방의 메시지 목록 조회
    // GET /api/chat/room/{roomId}/messages
    @GetMapping("/room/{roomId}/messages")
    public List<ChatMessageResponseDto> getMessages(
            @PathVariable Long roomId
    ) {
        return chatService.getMessages(roomId);
    }

    // 채팅방 나가기
    // DELETE /api/chat/room/{roomId}
    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<String> leaveRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails user
    ) {
        chatService.leaveRoom(roomId, user.getUsername());
        return ResponseEntity.ok("채팅방을 나갔습니다.");
    }
}
