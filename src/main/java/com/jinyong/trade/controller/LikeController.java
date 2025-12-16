package com.jinyong.trade.controller;

import com.jinyong.trade.dto.LikeDto;
import com.jinyong.trade.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    // 좋아요 토글 (좋아요 / 좋아요 취소)
    @PostMapping("/{postId}")
    public LikeDto toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String userId = userDetails.getUsername();
        return likeService.toggleLike(postId, userId);
    }

    // 좋아요 상태 조회 (로그인 사용자)
    @GetMapping("/{postId}")
    public LikeDto getLikeStatus(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            // 비로그인 사용자는 카운트만 반환
            return likeService.getLikeCount(postId);
        }
        String userId = userDetails.getUsername();
        return likeService.getLikeStatus(postId, userId);
    }
}