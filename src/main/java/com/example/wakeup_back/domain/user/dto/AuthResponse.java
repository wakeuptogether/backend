package com.example.wakeup_back.domain.user.dto;

import lombok.*;

@Getter
@Builder
public class AuthResponse {
    private Long userId;
    private String token;
    private String name;
    private String email;
}