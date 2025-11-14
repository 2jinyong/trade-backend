package com.jinyong.trade.controller;

import com.jinyong.trade.dto.LoginDto;
import com.jinyong.trade.entity.User;
import com.jinyong.trade.jwt.JwtUtil;
import com.jinyong.trade.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {
        Optional<User> userOpt = userRepository.findByUserId(loginDto.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("❌ 아이디를 찾을 수 없습니다.");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("❌ 비밀번호가 틀렸습니다.");
        }

        // ✅ JWT 생성
        String token = jwtUtil.createToken(user.getUserId(), user.getRole());

        // ✅ JWT를 HttpOnly 쿠키로 내려줌
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)           // JS 접근 방지
                .secure(true)             // HTTPS에서만 전송
                .path("/")                // 모든 경로에 적용
                .maxAge(Duration.ofHours(1)) // 1시간 유효
                .sameSite("Strict")       // CSRF 방지
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok("✅ 로그인 성공");
    }
}
