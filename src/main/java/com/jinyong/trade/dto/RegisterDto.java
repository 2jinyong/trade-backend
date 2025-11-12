package com.jinyong.trade.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String userId;
    private String password;
    private String name;
    private String tel;
    private String email;
}
