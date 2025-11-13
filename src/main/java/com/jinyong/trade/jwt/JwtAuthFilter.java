package com.jinyong.trade.jwt;

import com.jinyong.trade.service.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailService customUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // ✅ 쿠키에서 JWT 토큰 꺼내기
        String token = extractTokenFromCookies(request);

        // ✅ 토큰이 존재하고 유효한 경우
        if (token != null && jwtUtil.validateToken(token)) {

            // ✅ 토큰에서 사용자 이름 꺼냄
            String username = jwtUtil.getUsername(token);

            // ✅ DB에서 사용자 정보 조회
            UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

            // ✅ 인증 객체 생성 (비밀번호는 null, 권한은 userDetails에서 가져옴)
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // ✅ 요청 정보(IP, 세션 등)를 인증 객체에 추가
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // ✅ 인증 정보를 SecurityContext에 등록
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // ✅ 다음 필터 또는 컨트롤러로 요청 넘김
        filterChain.doFilter(request, response);
    }

    // ✅ 쿠키에서 "token" 이름의 JWT 꺼내는 메서드
    private String extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
