package com.jinyong.trade.service;

import com.jinyong.trade.entity.User;
import com.jinyong.trade.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final UserRepository userRepository;
    private final WebClient webClient = WebClient.create();

    /**
     * 구글 로그인 처리
     * 프론트에서 받은 accessToken으로 구글 사용자 정보 조회 후 로그인/회원가입 처리
     */
    public User googleLogin(String accessToken) {
        // 1. 구글 API로 사용자 정보 조회
        Map<String, Object> userInfo = webClient.get()
                .uri("https://www.googleapis.com/oauth2/v2/userinfo")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (userInfo == null) {
            throw new RuntimeException("구글 사용자 정보를 가져올 수 없습니다.");
        }

        String providerId = (String) userInfo.get("id");
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");

        // 2. 기존 사용자인지 확인 (provider + providerId로)
        Optional<User> existingUser = userRepository.findByProviderAndProviderId("google", providerId);

        if (existingUser.isPresent()) {
            // 기존 사용자 → 바로 반환
            return existingUser.get();
        }

        // 3. 이메일로 기존 사용자 확인 (로컬 계정이 있을 수 있음)
        Optional<User> userByEmail = userRepository.findByEmail(email);
        if (userByEmail.isPresent()) {
            // 기존 로컬 계정에 소셜 정보 연동
            User user = userByEmail.get();
            user.setProvider("google");
            user.setProviderId(providerId);
            return userRepository.save(user);
        }

        // 4. 신규 사용자 → 회원가입
        User newUser = new User();
        newUser.setUserId("google_" + providerId);
        newUser.setEmail(email);
        newUser.setName(name);
        newUser.setProvider("google");
        newUser.setProviderId(providerId);
        newUser.setRole("USER");
        // password, tel은 소셜 로그인이므로 null

        return userRepository.save(newUser);
    }

    /**
     * 네이버 로그인 처리
     */
    public User naverLogin(String accessToken) {
        // 1. 네이버 API로 사용자 정보 조회
        Map<String, Object> response = webClient.get()
                .uri("https://openapi.naver.com/v1/nid/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || !"00".equals(response.get("resultcode"))) {
            throw new RuntimeException("네이버 사용자 정보를 가져올 수 없습니다.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> userInfo = (Map<String, Object>) response.get("response");

        String providerId = (String) userInfo.get("id");
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");

        // 2. 기존 사용자인지 확인
        Optional<User> existingUser = userRepository.findByProviderAndProviderId("naver", providerId);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        // 3. 이메일로 기존 사용자 확인
        Optional<User> userByEmail = userRepository.findByEmail(email);
        if (userByEmail.isPresent()) {
            User user = userByEmail.get();
            user.setProvider("naver");
            user.setProviderId(providerId);
            return userRepository.save(user);
        }

        // 4. 신규 사용자
        User newUser = new User();
        newUser.setUserId("naver_" + providerId);
        newUser.setEmail(email);
        newUser.setName(name != null ? name : "네이버사용자");
        newUser.setProvider("naver");
        newUser.setProviderId(providerId);
        newUser.setRole("USER");

        return userRepository.save(newUser);
    }
}