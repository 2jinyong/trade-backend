package com.jinyong.trade.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDto {

    @NotBlank(message = "제목을 입력하세요")
    private String title;

    @Min(value = 1, message = "1원이상 가격을 정해주세요")
    private int price;

    @NotBlank(message = "가격을 입력하세요")
    private String content;
}
