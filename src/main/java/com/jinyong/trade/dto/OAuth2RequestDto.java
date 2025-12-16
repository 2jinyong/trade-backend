package com.jinyong.trade.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuth2RequestDto {
    private String accessToken;  // 소셜 서비스에서 받은 access token
}