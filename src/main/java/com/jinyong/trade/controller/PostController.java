package com.jinyong.trade.controller;

import com.jinyong.trade.dto.PostDto;
import com.jinyong.trade.entity.Post;
import com.jinyong.trade.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    // 게시글 작성
    @PostMapping
    public Post create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PostDto postDto
    ) {
        String username = userDetails.getUsername();
        return postService.save(postDto, username);
    }

    @PostMapping("/upload")
    public Map<String, String> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get("C:/jinyong/project/uploads/" + filename);


        Files.createDirectories(path.getParent());
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        // 정식 URL로 변환
        String url = "http://localhost:8081/uploads/" + filename;

        return Map.of("url", url);
    }



    // 전체 조회
    @GetMapping
    public List<Post> getAll() {
        return postService.findAll();
    }

    // 단건 조회
    @GetMapping("/{id}")
    public Post getOne(@PathVariable Long id) {
        return postService.findById(id);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        postService.delete(id);
    }
}
