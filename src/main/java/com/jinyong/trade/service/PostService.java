package com.jinyong.trade.service;

import com.jinyong.trade.dto.PostDto;
import com.jinyong.trade.entity.Post;
import com.jinyong.trade.repository.PostRepository;
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
}
