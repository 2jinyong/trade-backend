package com.jinyong.trade.service;

import com.jinyong.trade.dto.PaymentConfirmDto;
import com.jinyong.trade.dto.PaymentReadyResponseDto;
import com.jinyong.trade.dto.PaymentRequestDto;
import com.jinyong.trade.entity.User;
import com.jinyong.trade.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final WalletService walletService;
    private final UserRepository userRepository;

    @Value("${toss.payments.secret-key}")
    private String secretKey;

    @Value("${toss.payments.client-key}")
    private String clientKey;

    @Value("${toss.payments.dev-mode:true}")
    private boolean devMode;

    private static final String TOSS_API_URL = "https://api.tosspayments.com/v1/payments/confirm";

    // 결제 준비 (주문 정보 생성)
    public PaymentReadyResponseDto preparePayment(String userId, PaymentRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String orderId = "ORDER_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        return PaymentReadyResponseDto.builder()
                .orderId(orderId)
                .amount(request.getAmount())
                .orderName("인앱머니 충전")
                .customerName(user.getName())
                .build();
    }

    // 토스 결제 승인
    @Transactional
    public Map<String, Object> confirmPayment(String userId, PaymentConfirmDto request) {
        try {
            // 개발 모드: 실제 토스 API 호출 없이 바로 성공 처리
            if (devMode) {
                log.info("개발 모드: 토스 API 호출 건너뛰기 - 바로 충전 처리");

                // 바로 충전 처리
                walletService.charge(userId, request.getAmount(), request.getPaymentKey(), request.getOrderId());

                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "충전이 완료되었습니다.");
                result.put("amount", request.getAmount());
                result.put("paymentKey", request.getPaymentKey());
                result.put("orderId", request.getOrderId());
                return result;
            }

            // 프로덕션 모드: 실제 토스 결제 승인 API 호출
            WebClient webClient = WebClient.builder()
                    .baseUrl(TOSS_API_URL)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " +
                            Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8)))
                    .build();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("paymentKey", request.getPaymentKey());
            requestBody.put("orderId", request.getOrderId());
            requestBody.put("amount", request.getAmount().intValue());

            Map<String, Object> response = webClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && "DONE".equals(response.get("status"))) {
                // 결제 성공 시 충전 처리
                walletService.charge(userId, request.getAmount(), request.getPaymentKey(), request.getOrderId());

                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "충전이 완료되었습니다.");
                result.put("amount", request.getAmount());
                result.put("paymentKey", request.getPaymentKey());
                result.put("orderId", request.getOrderId());
                return result;
            } else {
                throw new RuntimeException("결제 승인에 실패했습니다.");
            }

        } catch (Exception e) {
            log.error("토스 결제 승인 실패: {}", e.getMessage());
            throw new RuntimeException("결제 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 클라이언트 키 반환 (프론트엔드에서 사용)
    public String getClientKey() {
        return clientKey;
    }
}
