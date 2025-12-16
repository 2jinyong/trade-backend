package com.jinyong.trade.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @Column(nullable = false, unique = true, length = 100)
    private String userId;

    @Column(length = 100)
    private String password;  // 소셜 로그인은 비밀번호 없음

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 15)
    private String tel;  // 소셜 로그인은 전화번호 없을 수 있음

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 10)
    private String role;

    // 소셜 로그인 관련 필드
    @Column(length = 20)
    private String provider;  // "google", "naver", "local"

    @Column(length = 100)
    private String providerId;  // 소셜 서비스에서 제공하는 고유 ID
}
