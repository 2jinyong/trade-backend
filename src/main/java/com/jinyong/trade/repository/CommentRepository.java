package com.jinyong.trade.repository;

import com.jinyong.trade.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // JpaRepository<엔티티, PK타입>

    // 메서드 이름만으로 SQL 자동 생성
    // SELECT * FROM comment WHERE post_id = ? ORDER BY created_at ASC
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);
}
