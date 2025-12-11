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
}