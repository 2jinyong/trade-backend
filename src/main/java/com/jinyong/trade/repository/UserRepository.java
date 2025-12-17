package com.jinyong.trade.repository;

import com.jinyong.trade.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);
    Optional<User> findByUserId(String userId);
    Optional<User> findByName(String username);

    // 소셜 로그인용
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
    Optional<User> findByEmail(String email);
}
