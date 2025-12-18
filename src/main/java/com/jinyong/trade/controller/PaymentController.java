package com.jinyong.trade.controller;

import com.jinyong.trade.dto.PaymentConfirmDto;
import com.jinyong.trade.dto.PaymentReadyResponseDto;
import com.jinyong.trade.dto.PaymentRequestDto;
import com.jinyong.trade.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 토스 클라이언트 키 조회 (프론트엔드에서 토스 위젯 초기화용)
    @GetMapping("/client-key")
    public ResponseEntity<Map<String, String>> getClientKey() {
        Map<String, String> response = new HashMap<>();
        response.put("clientKey", paymentService.getClientKey());
        return ResponseEntity.ok(response);
    }

    // 결제 준비 (주문 정보 생성)
    @PostMapping("/prepare")
    public ResponseEntity<PaymentReadyResponseDto> preparePayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PaymentRequestDto request) {
        PaymentReadyResponseDto response = paymentService.preparePayment(userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }

    // 토스 결제 승인 (결제 완료 후 호출)
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PaymentConfirmDto request) {
        try {
            Map<String, Object> result = paymentService.confirmPayment(userDetails.getUsername(), request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
