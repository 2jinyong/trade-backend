package com.jinyong.trade.initializer;

import com.jinyong.trade.entity.User;
import com.jinyong.trade.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 관리자 계정이 없으면 생성
        if (!userRepository.existsByUserId("admin")) {
            User admin = new User();
            admin.setUserId("admin");
            admin.setPassword(passwordEncoder.encode("admin1234")); // 반드시 암호화
            admin.setName("admin");
            admin.setTel("1234567890");
            admin.setEmail("admin@admin");
            admin.setRole("ADMIN");
            userRepository.save(admin);
            System.out.println("✅ 관리자 계정 생성 완료");
        }
    }
}