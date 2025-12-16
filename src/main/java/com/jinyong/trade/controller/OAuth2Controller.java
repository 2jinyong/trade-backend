package com.jinyong.trade.controller;

import com.jinyong.trade.dto.OAuth2RequestDto;
import com.jinyong.trade.entity.User;
import com.jinyong.trade.jwt.JwtUtil;
import com.jinyong.trade.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth2")
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;
    private final JwtUtil jwtUtil;

    /**
     * 구글 로그인
     * 프론트에서 구글 로그인 후 받은 accessToken을 전송
     */
    @PostMapping("/google")
    public ResponseEntity<String> googleLogin(
            @RequestBody OAuth2RequestDto request,
            HttpServletResponse response
    ) {
        try {
            // 1. 구글 로그인 처리 (사용자 정보 조회 + 회원가입/로그인)
            User user = oAuth2Service.googleLogin(request.getAccessToken());

            // 2. JWT 생성
            String token = jwtUtil.createToken(user.getUserId(), user.getRole());

            // 3. 쿠키로 JWT 전달
            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("None")
                    .maxAge(Duration.ofHours(1))
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok("구글 로그인 성공");

        } catch (Exception e) {
            return ResponseEntity.status(401).body("구글 로그인 실패: " + e.getMessage());
        }
    }

    /**
     * 네이버 로그인
     */
    @PostMapping("/naver")
    public ResponseEntity<String> naverLogin(
            @RequestBody OAuth2RequestDto request,
            HttpServletResponse response
    ) {
        try {
            User user = oAuth2Service.naverLogin(request.getAccessToken());

            String token = jwtUtil.createToken(user.getUserId(), user.getRole());

            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("None")
                    .maxAge(Duration.ofHours(1))
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            return ResponseEntity.ok("네이버 로그인 성공");

        } catch (Exception e) {
            return ResponseEntity.status(401).body("네이버 로그인 실패: " + e.getMessage());
        }
    }
}