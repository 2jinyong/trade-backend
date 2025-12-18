package com.jinyong.trade.repository;

import com.jinyong.trade.entity.Transaction;
import com.jinyong.trade.entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByWalletOrderByCreatedAtDesc(Wallet wallet, Pageable pageable);

    Page<Transaction> findByWalletAndTypeOrderByCreatedAtDesc(Wallet wallet, Transaction.TransactionType type, Pageable pageable);

    Optional<Transaction> findByOrderId(String orderId);

    Optional<Transaction> findByPaymentKey(String paymentKey);
}
