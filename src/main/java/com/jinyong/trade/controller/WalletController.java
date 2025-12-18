package com.jinyong.trade.controller;

import com.jinyong.trade.dto.*;
import com.jinyong.trade.entity.Transaction;
import com.jinyong.trade.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    // 지갑 조회 (없으면 생성)
    @GetMapping
    public ResponseEntity<WalletResponseDto> getWallet(@AuthenticationPrincipal UserDetails userDetails) {
        WalletResponseDto wallet = walletService.getOrCreateWallet(userDetails.getUsername());
        return ResponseEntity.ok(wallet);
    }

    // 잔액 조회
    @GetMapping("/balance")
    public ResponseEntity<Map<String, BigDecimal>> getBalance(@AuthenticationPrincipal UserDetails userDetails) {
        BigDecimal balance = walletService.getBalance(userDetails.getUsername());
        Map<String, BigDecimal> response = new HashMap<>();
        response.put("balance", balance);
        return ResponseEntity.ok(response);
    }

    // 송금
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransferRequestDto request) {
        try {
            TransactionResponseDto transaction = walletService.transfer(userDetails.getUsername(), request);
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // 출금 요청
    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody WithdrawRequestDto request) {
        try {
            TransactionResponseDto transaction = walletService.withdraw(userDetails.getUsername(), request);
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // 거래내역 조회
    @GetMapping("/transactions")
    public ResponseEntity<Page<TransactionResponseDto>> getTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionResponseDto> transactions = walletService.getTransactions(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(transactions);
    }

    // 거래유형별 내역 조회
    @GetMapping("/transactions/{type}")
    public ResponseEntity<Page<TransactionResponseDto>> getTransactionsByType(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Transaction.TransactionType transactionType = Transaction.TransactionType.valueOf(type.toUpperCase());
            Pageable pageable = PageRequest.of(page, size);
            Page<TransactionResponseDto> transactions = walletService.getTransactionsByType(
                    userDetails.getUsername(), transactionType, pageable);
            return ResponseEntity.ok(transactions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
