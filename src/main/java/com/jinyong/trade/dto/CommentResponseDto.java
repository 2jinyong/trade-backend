package com.jinyong.trade.dto;

import com.jinyong.trade.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {

    private Long id;
    private String content;
    private String username;
    private LocalDateTime createdAt;

    // 엔티티를 그대로 보내면
    // 무한 참조 / 보안 문제 발생 가능
    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.username = comment.getUser().getUserId();
        this.createdAt = comment.getCreatedAt();
    }
}

