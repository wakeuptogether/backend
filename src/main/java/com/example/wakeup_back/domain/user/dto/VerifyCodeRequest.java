package com.example.wakeup_back.domain.user.dto;

import lombok.Getter;

@Getter
public class VerifyCodeRequest {
    private String email;
    private String code;
}