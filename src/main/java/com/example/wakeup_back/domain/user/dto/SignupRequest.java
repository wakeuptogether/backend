// domain/user/dto/SignupRequest.java
package com.example.wakeup_back.domain.user.dto;

import lombok.Getter;

@Getter
public class SignupRequest {
    private String email;
    private String password;
    private String name;
    // code 없음 — send-code → verify 후 signup 흐름
}