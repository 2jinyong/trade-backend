package com.jinyong.trade.dto;

import com.jinyong.trade.entity.Wallet;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class WalletResponseDto {

    private Long id;
    private String userId;
    private String userName;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static WalletResponseDto from(Wallet wallet) {
        return WalletResponseDto.builder()
                .id(wallet.getId())
                .userId(wallet.getUser().getUserId())
                .userName(wallet.getUser().getName())
                .balance(wallet.getBalance())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }
}
