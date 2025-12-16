package com.jinyong.trade.service;

import com.jinyong.trade.dto.PostDto;
import com.jinyong.trade.dto.PostResponseDto;
import com.jinyong.trade.entity.Post;
import com.jinyong.trade.repository.LikeRepository;
import com.jinyong.trade.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;

    public Post save(PostDto dto, String username) {
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setPrice(dto.getPrice());
        post.setContent(dto.getContent());
        post.setUserId(username); // ✅ 로그인 사용자 이름으로 작성자 설정

        return postRepository.save(post);
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    // 좋아요 카운트 포함한 목록 조회
    public List<PostResponseDto> findAllWithLikeCount() {
        return postRepository.findAll().stream()
                .map(post -> {
                    long likeCount = likeRepository.countByPostId(post.getId());
                    return PostResponseDto.from(post, likeCount);
                })
                .collect(Collectors.toList());
    }

    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow();
    }

    public void delete(Long id) {
        postRepository.deleteById(id);
    }

    @Transactional
    public Post increaseViews(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        post.setViews(post.getViews() + 1);
        return post;
    }

    @Transactional
    public Post update(Long id, PostDto dto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        post.setTitle(dto.getTitle());
        post.setPrice(dto.getPrice());
        post.setContent(dto.getContent());

        return post;
    }

}
