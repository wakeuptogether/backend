package com.example.wakeup_back.domain.user.controller;

import com.example.wakeup_back.domain.user.dto.*;
import com.example.wakeup_back.domain.user.service.AuthService;
import com.example.wakeup_back.domain.user.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // 인증 코드 전송
    @PostMapping("/send-code")
    public String sendCode(@RequestBody SendCodeRequest request) {
        emailService.sendVerificationCode(request.getEmail());
        return "인증 코드가 전송되었습니다.";
    }

    // 인증 코드 검증
    @PostMapping("/verify-code")
    public String verifyCode(@RequestBody VerifyCodeRequest request) {
        emailService.verifyCode(request.getEmail(), request.getCode());
        return "인증이 완료되었습니다.";
    }

    // 회원가입
    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) {
        authService.signup(request);
        return "회원가입 성공";
    }
}