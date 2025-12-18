package com.jinyong.trade.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferRequestDto {

    @NotBlank(message = "받는 사람 ID는 필수입니다.")
    private String receiverUserId;

    @NotNull(message = "송금 금액은 필수입니다.")
    @Min(value = 1, message = "송금 금액은 1원 이상이어야 합니다.")
    private BigDecimal amount;

    private String memo;
}
