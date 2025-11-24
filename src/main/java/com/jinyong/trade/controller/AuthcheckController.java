package com.jinyong.trade.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthcheckController {

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkAuth(
            @CookieValue(value = "token", required = false) String token
    ) {
        boolean authenticated = (token != null && !token.isEmpty());
        return ResponseEntity.ok(Map.of("authenticated", authenticated));
    }
}
