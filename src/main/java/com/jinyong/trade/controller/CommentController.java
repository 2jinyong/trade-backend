package com.jinyong.trade.controller;

import com.jinyong.trade.dto.CommentResponseDto;
import com.jinyong.trade.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController // JSON 응답 컨트롤러
@RequestMapping("/api/comments") // 공통 URL
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @PostMapping("/{postId}")
    public ResponseEntity<?> create(
            @PathVariable Long postId, // URL 경로 변수
            @RequestBody Map<String, String> body, // JSON body
            @AuthenticationPrincipal UserDetails user // 로그인 사용자 정보
    ) {

        // body.get("content") -> { "content": "댓글 내용" }
        commentService.create(postId, body.get("content"), user.getUsername());

        return ResponseEntity.ok().build();
    }

    // 댓글 목록 조회
    @GetMapping("/{postId}")
    public List<CommentResponseDto> list(
            @PathVariable Long postId
    ) {

        // 엔티티 -> DTO 변환 후 반환
        return commentService.getComments(postId)
                .stream()
                .map(CommentResponseDto::new)
                .toList();
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<?> update(
            @PathVariable Long commentId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails user
    ) {

        commentService.update(commentId, body.get("content"), user.getUsername());
        return ResponseEntity.ok().build();
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> delete(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails user
    ) {

        commentService.delete(commentId, user.getUsername());
        return ResponseEntity.ok().build();
    }
}
