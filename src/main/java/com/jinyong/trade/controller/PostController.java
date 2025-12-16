package com.jinyong.trade.controller;

import com.jinyong.trade.dto.PostDto;
import com.jinyong.trade.dto.PostResponseDto;
import com.jinyong.trade.entity.Post;
import com.jinyong.trade.service.PostService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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



    // 전체 조회 (좋아요 카운트 포함)
    @GetMapping
    public List<PostResponseDto> getAll() {
        return postService.findAllWithLikeCount();
    }

    // 단건 조회 + 조회수 자동 증가 (쿠키 기반 중복 방지)
    @GetMapping("/{id}")
    public Post getOne(
            @PathVariable Long id,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String cookieName = "viewed_post_" + id;
        boolean alreadyViewed = false;

        // 쿠키 확인
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    if(cookie.getValue().equals("true")) {
                        alreadyViewed = true;
                        break;
                    }
                }
            }
        }

        // 조회한 적 없으면 조회수 증가 + 쿠키 생성
        if (!alreadyViewed) {
            postService.increaseViews(id);

            // 한국 시간 기준 자정까지 남은 시간 계산 (초 단위)
            ZoneId koreaZone = ZoneId.of("Asia/Seoul");
            LocalDateTime now = LocalDateTime.now(koreaZone);
            LocalDateTime midnight = LocalDateTime.of(LocalDate.now(koreaZone).plusDays(1), LocalTime.MIDNIGHT);
            long secondsUntilMidnight = ChronoUnit.SECONDS.between(now, midnight);

//            System.out.println("=== 쿠키 만료 시간 디버그 ===");
//            System.out.println("한국 현재 시간: " + now);
//            System.out.println("한국 자정: " + midnight);
//            System.out.println("남은 초: " + secondsUntilMidnight);
//            System.out.println("남은 시간: " + (secondsUntilMidnight / 3600) + "시간 " + ((secondsUntilMidnight % 3600) / 60) + "분");

            Cookie viewCookie = new Cookie(cookieName, "true");
            viewCookie.setMaxAge((int) secondsUntilMidnight); // 자정까지 유지
            viewCookie.setPath("/");
            response.addCookie(viewCookie);
        }

        return postService.findById(id);
    }

    // 수정
    @PutMapping("/{id}")
    public Post update(@PathVariable Long id, @Valid @RequestBody PostDto postDto) {
        return postService.update(id, postDto);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        postService.delete(id);
    }
}
