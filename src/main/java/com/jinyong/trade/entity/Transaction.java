package com.jinyong.trade.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
public class Transaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionType type;

    @Column(nullable = false, precision = 15, scale = 0)
    private BigDecimal amount;

    @Column(precision = 15, scale = 0)
    private BigDecimal balanceAfter;

    // 송금 시 상대방 정보
    @Column(length = 100)
    private String counterpartyUserId;

    @Column(length = 50)
    private String counterpartyName;

    // 토스 결제 관련
    @Column(length = 100)
    private String paymentKey;

    @Column(length = 100)
    private String orderId;

    // 출금 관련
    @Column(length = 20)
    private String bankCode;

    @Column(length = 20)
    private String accountNumber;

    @Column(length = 50)
    private String accountHolder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status = TransactionStatus.COMPLETED;

    @Column(length = 200)
    private String memo;

    public enum TransactionType {
        CHARGE,     // 충전 (토스 결제)
        TRANSFER_OUT,   // 송금 (보낸 것)
        TRANSFER_IN,    // 송금 (받은 것)
        WITHDRAW    // 출금
    }

    public enum TransactionStatus {
        PENDING,    // 대기중
        COMPLETED,  // 완료
        FAILED,     // 실패
        CANCELLED   // 취소
    }
}
