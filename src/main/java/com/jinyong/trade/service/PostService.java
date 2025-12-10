package com.jinyong.trade.service;

import com.jinyong.trade.dto.PostDto;
import com.jinyong.trade.entity.Post;
import com.jinyong.trade.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

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
