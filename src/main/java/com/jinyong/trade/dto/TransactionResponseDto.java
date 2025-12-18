package com.jinyong.trade.dto;

import com.jinyong.trade.entity.Transaction;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class TransactionResponseDto {

    private Long id;
    private String type;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String counterpartyUserId;
    private String counterpartyName;
    private String status;
    private String memo;
    private LocalDateTime createdAt;

    public static TransactionResponseDto from(Transaction transaction) {
        return TransactionResponseDto.builder()
                .id(transaction.getId())
                .type(transaction.getType().name())
                .amount(transaction.getAmount())
                .balanceAfter(transaction.getBalanceAfter())
                .counterpartyUserId(transaction.getCounterpartyUserId())
                .counterpartyName(transaction.getCounterpartyName())
                .status(transaction.getStatus().name())
                .memo(transaction.getMemo())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
