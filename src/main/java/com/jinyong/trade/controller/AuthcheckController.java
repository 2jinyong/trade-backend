package com.jinyong.trade.controller;

import com.jinyong.trade.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthcheckController {

    private final JwtUtil jwtUtil;

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAuth(
            @CookieValue(value = "token", required = false) String token
    ) {
        Map<String, Object> response = new HashMap<>();

        if (token == null || token.isEmpty() || !jwtUtil.validateToken(token)) {
            response.put("authenticated", false);
            return ResponseEntity.ok(response);
        }

        response.put("authenticated", true);
        response.put("userId", jwtUtil.getUsername(token));

        // email이 있으면 추가 (소셜 로그인인 경우)
        String email = jwtUtil.getEmail(token);
        if (email != null) {
            response.put("email", email);
        }

        return ResponseEntity.ok(response);
    }
}
