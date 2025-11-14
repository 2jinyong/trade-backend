package com.jinyong.trade.controller;

import com.jinyong.trade.entity.Post;
import com.jinyong.trade.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    // 게시글 작성 (Create)
    @PostMapping
    public Post create(@RequestBody Post post) {
        return postService.save(post);
    }

    // 게시글 전체 조회 (Read - 목록)
    @GetMapping
    public List<Post> getAll() {
        return postService.findAll();
    }

    // 게시글 단건 조회 (Read - 상세)
    @GetMapping("/{id}")
    public Post getOne(@PathVariable Long id) {
        return postService.findById(id);
    }

    // 게시글 삭제 (Delete)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        postService.delete(id);
    }
}
