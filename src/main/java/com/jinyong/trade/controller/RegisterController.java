package com.jinyong.trade.controller;

import com.jinyong.trade.dto.RegisterDto;
import com.jinyong.trade.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RegisterController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto dto) {
        if (userService.isUserIdDuplicate(dto.getUserId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 아이디입니다.");
        }
        if(userService.isEmailDuplicate(dto.getEmail())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("중복된 이메일입니다.");
        }

        userService.registerUser(dto);
        return ResponseEntity.ok("회원가입 성공");
    }
}
