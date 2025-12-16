package com.jinyong.trade.repository;

import com.jinyong.trade.entity.Like;
import com.jinyong.trade.entity.Post;
import com.jinyong.trade.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    // 이미 좋아요 했는지 여부 확인용
    boolean existsByUserAndPost(User user, Post post);

    // 토글 OFF할 때 삭제용
    Optional<Like> findByUserAndPost(User user, Post post);

    // 좋아요 개수 보여줄 때
    long countByPost(Post post);

    // postId로 좋아요 개수 조회
    long countByPostId(Long postId);
}
