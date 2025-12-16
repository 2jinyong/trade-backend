package com.jinyong.trade.config;

import com.jinyong.trade.entity.User;
import com.jinyong.trade.jwt.JwtUtil;
import com.jinyong.trade.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oauthToken.getPrincipal();
        String provider = oauthToken.getAuthorizedClientRegistrationId(); // google, naver

        String providerId;
        String email;
        String name;

        if ("google".equals(provider)) {
            providerId = oAuth2User.getAttribute("sub");
            email = oAuth2User.getAttribute("email");
            name = oAuth2User.getAttribute("name");
        } else if ("naver".equals(provider)) {
            Map<String, Object> responseMap = oAuth2User.getAttribute("response");
            providerId = (String) responseMap.get("id");
            email = (String) responseMap.get("email");
            name = (String) responseMap.get("name");
        } else {
            throw new IllegalArgumentException("지원하지 않는 OAuth2 Provider: " + provider);
        }

        // DB에서 사용자 조회 또는 생성
        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    // 신규 사용자 생성 - userId를 이메일로 설정
                    User newUser = new User();
                    newUser.setUserId(email);
                    newUser.setEmail(email);
                    newUser.setName(name != null ? name : "사용자");
                    newUser.setRole("user");
                    newUser.setProvider(provider);
                    newUser.setProviderId(providerId);
                    return userRepository.save(newUser);
                });

        // JWT 토큰 생성 (소셜 로그인은 email 포함)
        String token = jwtUtil.createToken(user.getUserId(), user.getRole(), user.getEmail());

        // 쿠키로 JWT 전달
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(Duration.ofHours(1))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 프론트엔드로 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, "http://localhost:3000");
    }
}
