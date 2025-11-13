package com.jinyong.trade.service;

import com.jinyong.trade.entity.User;
import com.jinyong.trade.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    // Spring Security가 사용자 인증을 위해 호출하는 메서드
    // username은 JWT에서 꺼낸 사용자 ID
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // DB에서 사용자 정보 조회
        User user = userRepository.findByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // 조회된 사용자 정보를 기반으로 UserDetails 객체 생성
        // Spring Security가 이해할 수 있는 형식으로 감싸줌
        return new org.springframework.security.core.userdetails.User(
                user.getUserId(),                  // 사용자 ID
                user.getPassword(),                  // 비밀번호 (우린 JWT 인증이라 사용 안 함)
                Collections.singletonList(           // 권한 정보
                        new SimpleGrantedAuthority(user.getRole()) // 예: ROLE_USER, ROLE_ADMIN
                )
        );
    }
}
