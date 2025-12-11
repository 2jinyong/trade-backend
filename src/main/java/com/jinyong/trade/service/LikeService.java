package com.jinyong.trade.service;

import com.jinyong.trade.dto.LikeDto;
import com.jinyong.trade.entity.Like;
import com.jinyong.trade.entity.Post;
import com.jinyong.trade.entity.User;
import com.jinyong.trade.repository.LikeRepository;
import com.jinyong.trade.repository.PostRepository;
import com.jinyong.trade.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public LikeDto toggleLike(Long postId, String userId) {

        // 1. userId로 User 조회
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2. postId로 Post 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 3. 이미 좋아요 했는지 체크
        boolean alreadyLiked = likeRepository.existsByUserAndPost(user, post);

        boolean liked;

        if (alreadyLiked) {
            // 4. 이미 좋아요 되어 있으면 → 삭제 (좋아요 취소)
            Like like = likeRepository.findByUserAndPost(user, post)
                    .orElseThrow(() -> new RuntimeException("좋아요 정보를 찾을 수 없습니다."));
            likeRepository.delete(like);
            liked = false;
        } else {
            // 5. 좋아요 안 되어 있으면 → 생성
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
            liked = true;
        }

        // 6. 현재 좋아요 개수 조회
        long likeCount = likeRepository.countByPost(post);

        // 7. 결과 반환
        return new LikeDto(liked, likeCount);
    }
}