package com.jinyong.trade.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class WithdrawRequestDto {

    @NotNull(message = "출금 금액은 필수입니다.")
    @Min(value = 1000, message = "출금 금액은 1,000원 이상이어야 합니다.")
    private BigDecimal amount;

    @NotBlank(message = "은행 코드는 필수입니다.")
    private String bankCode;

    @NotBlank(message = "계좌번호는 필수입니다.")
    private String accountNumber;

    @NotBlank(message = "예금주는 필수입니다.")
    private String accountHolder;
}
