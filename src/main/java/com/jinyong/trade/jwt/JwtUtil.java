package com.jinyong.trade.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    // 문자열 비밀키를 Key 객체로 변환하는 메서드
    private Key getSigningKey() {
        // HMAC-SHA 방식으로 서명할 수 있는 키로 변환
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }


    // JWT 생성
    public String createToken(String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 3600000); // 1시간 후

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT 토큰이 유효한지 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            // JWT 파서 빌더를 생성하고 서명 키를 설정
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // 비밀키(Key 객체)를 설정해서 서명 검증
                    .build()                        // 파서 객체 생성
                    .parseClaimsJws(token);         // 토큰을 파싱해서 서명, 만료, 형식 등을 검사

            // 검증 성공 → 유효한 토큰
            return true;
        } catch (Exception e) {
            // 검증 실패 → 만료, 위조, 형식 오류 등
            return false;
        }
    }

    // JWT 토큰에서 사용자 이름(username)을 꺼내는 메서드
    public String getUsername(String token) {
        // 토큰을 파싱해서 Claims(내용) 객체를 얻음
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // 비밀키(Key 객체)를 설정해서 서명 검증
                .build()                        // 파서 객체 생성
                .parseClaimsJws(token)          // 토큰을 파싱해서 서명, 만료, 형식 등을 검사
                .getBody();                     // 실제 내용(Claims)을 꺼냄

        // Claims에서 subject(=username) 꺼내서 반환
        return claims.getSubject();
    }

    public String getRole(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("role", String.class);
    }

}
