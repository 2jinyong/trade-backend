package com.jinyong.trade.service;

import com.jinyong.trade.dto.*;
import com.jinyong.trade.entity.Transaction;
import com.jinyong.trade.entity.User;
import com.jinyong.trade.entity.Wallet;
import com.jinyong.trade.repository.TransactionRepository;
import com.jinyong.trade.repository.UserRepository;
import com.jinyong.trade.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    // 지갑 조회 (없으면 생성)
    @Transactional
    public WalletResponseDto getOrCreateWallet(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Wallet wallet = walletRepository.findByUserUserId(userId)
                .orElseGet(() -> {
                    Wallet newWallet = new Wallet(user);
                    return walletRepository.save(newWallet);
                });

        return WalletResponseDto.from(wallet);
    }

    // 잔액 조회
    @Transactional(readOnly = true)
    public BigDecimal getBalance(String userId) {
        Wallet wallet = walletRepository.findByUserUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("지갑을 찾을 수 없습니다."));
        return wallet.getBalance();
    }

    // 송금
    @Transactional
    public TransactionResponseDto transfer(String senderUserId, TransferRequestDto request) {
        // 자기 자신에게 송금 불가
        if (senderUserId.equals(request.getReceiverUserId())) {
            throw new IllegalArgumentException("자기 자신에게 송금할 수 없습니다.");
        }

        // 송금자 지갑 조회 (락 적용)
        Wallet senderWallet = walletRepository.findByUserUserIdWithLock(senderUserId)
                .orElseThrow(() -> new IllegalArgumentException("송금자 지갑을 찾을 수 없습니다."));

        // 수신자 지갑 조회 (락 적용)
        Wallet receiverWallet = walletRepository.findByUserUserIdWithLock(request.getReceiverUserId())
                .orElseThrow(() -> new IllegalArgumentException("받는 사람을 찾을 수 없습니다."));

        // 잔액 확인
        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }

        // 송금 처리
        senderWallet.withdraw(request.getAmount());
        receiverWallet.deposit(request.getAmount());

        // 송금자 거래내역 저장
        Transaction senderTransaction = new Transaction();
        senderTransaction.setWallet(senderWallet);
        senderTransaction.setType(Transaction.TransactionType.TRANSFER_OUT);
        senderTransaction.setAmount(request.getAmount());
        senderTransaction.setBalanceAfter(senderWallet.getBalance());
        senderTransaction.setCounterpartyUserId(request.getReceiverUserId());
        senderTransaction.setCounterpartyName(receiverWallet.getUser().getName());
        senderTransaction.setMemo(request.getMemo());
        senderTransaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transactionRepository.save(senderTransaction);

        // 수신자 거래내역 저장
        Transaction receiverTransaction = new Transaction();
        receiverTransaction.setWallet(receiverWallet);
        receiverTransaction.setType(Transaction.TransactionType.TRANSFER_IN);
        receiverTransaction.setAmount(request.getAmount());
        receiverTransaction.setBalanceAfter(receiverWallet.getBalance());
        receiverTransaction.setCounterpartyUserId(senderUserId);
        receiverTransaction.setCounterpartyName(senderWallet.getUser().getName());
        receiverTransaction.setMemo(request.getMemo());
        receiverTransaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transactionRepository.save(receiverTransaction);

        return TransactionResponseDto.from(senderTransaction);
    }

    // 출금 요청
    @Transactional
    public TransactionResponseDto withdraw(String userId, WithdrawRequestDto request) {
        Wallet wallet = walletRepository.findByUserUserIdWithLock(userId)
                .orElseThrow(() -> new IllegalArgumentException("지갑을 찾을 수 없습니다."));

        // 잔액 확인
        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }

        // 출금 처리
        wallet.withdraw(request.getAmount());

        // 거래내역 저장
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setType(Transaction.TransactionType.WITHDRAW);
        transaction.setAmount(request.getAmount());
        transaction.setBalanceAfter(wallet.getBalance());
        transaction.setBankCode(request.getBankCode());
        transaction.setAccountNumber(request.getAccountNumber());
        transaction.setAccountHolder(request.getAccountHolder());
        transaction.setStatus(Transaction.TransactionStatus.PENDING); // 출금은 대기 상태로
        transactionRepository.save(transaction);

        return TransactionResponseDto.from(transaction);
    }

    // 충전 (PaymentService에서 호출)
    @Transactional
    public void charge(String userId, BigDecimal amount, String paymentKey, String orderId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Wallet wallet = walletRepository.findByUserUserIdWithLock(userId)
                .orElseGet(() -> {
                    Wallet newWallet = new Wallet(user);
                    return walletRepository.save(newWallet);
                });

        // 충전
        wallet.deposit(amount);

        // 거래내역 저장
        Transaction transaction = new Transaction();
        transaction.setWallet(wallet);
        transaction.setType(Transaction.TransactionType.CHARGE);
        transaction.setAmount(amount);
        transaction.setBalanceAfter(wallet.getBalance());
        transaction.setPaymentKey(paymentKey);
        transaction.setOrderId(orderId);
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);
    }

    // 거래내역 조회
    @Transactional(readOnly = true)
    public Page<TransactionResponseDto> getTransactions(String userId, Pageable pageable) {
        Wallet wallet = walletRepository.findByUserUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("지갑을 찾을 수 없습니다."));

        return transactionRepository.findByWalletOrderByCreatedAtDesc(wallet, pageable)
                .map(TransactionResponseDto::from);
    }

    // 거래유형별 내역 조회
    @Transactional(readOnly = true)
    public Page<TransactionResponseDto> getTransactionsByType(String userId, Transaction.TransactionType type, Pageable pageable) {
        Wallet wallet = walletRepository.findByUserUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("지갑을 찾을 수 없습니다."));

        return transactionRepository.findByWalletAndTypeOrderByCreatedAtDesc(wallet, type, pageable)
                .map(TransactionResponseDto::from);
    }
}
