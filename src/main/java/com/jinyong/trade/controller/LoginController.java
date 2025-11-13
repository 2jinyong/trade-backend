package com.jinyong.trade.controller;

import com.jinyong.trade.dto.LoginDto;
import com.jinyong.trade.entity.User;
import com.jinyong.trade.jwt.JwtUtil;
import com.jinyong.trade.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        Optional<User> userOpt = userRepository.findByUserId(loginDto.getUserId());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("❌ 아이디를 찾을 수 없습니다.");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("❌ 비밀번호가 틀렸습니다.");
        }

        // ✅ JWT 발급
        String token = jwtUtil.createToken(user.getUserId(), user.getRole());

        // ✅ 토큰을 응답으로 전달
        return ResponseEntity.ok(token);
    }
}
