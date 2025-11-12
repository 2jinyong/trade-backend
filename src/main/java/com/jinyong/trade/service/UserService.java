package com.jinyong.trade.service;

import com.jinyong.trade.dto.RegisterDto;
import com.jinyong.trade.entity.User;
import com.jinyong.trade.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(RegisterDto dto) {
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setTel(dto.getTel());
        user.setEmail(dto.getEmail());
        user.setRole("user");

        userRepository.save(user);
    }

    public boolean isUserIdDuplicate(String userId) {
        return userRepository.existsByUserId(userId);
    }

    public boolean isEmailDuplicate(String email){
        return userRepository.existsByEmail(email);
    }
}
