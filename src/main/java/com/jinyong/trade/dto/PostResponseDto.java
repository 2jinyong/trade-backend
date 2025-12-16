package com.jinyong.trade.dto;

import com.jinyong.trade.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostResponseDto {
    private Long id;
    private String userId;
    private String title;
    private String content;
    private int price;
    private int views;
    private long likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostResponseDto from(Post post, long likeCount) {
        return PostResponseDto.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .content(post.getContent())
                .price(post.getPrice())
                .views(post.getViews())
                .likeCount(likeCount)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}