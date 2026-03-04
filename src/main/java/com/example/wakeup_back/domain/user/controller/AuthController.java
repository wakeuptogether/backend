package com.example.wakeup_back.domain.user.controller;

import com.example.wakeup_back.domain.user.dto.AuthResponse;
import com.example.wakeup_back.domain.user.dto.LoginRequest;
import com.example.wakeup_back.domain.user.dto.SignupRequest;
import com.example.wakeup_back.domain.user.service.AuthService;
import com.example.wakeup_back.domain.user.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;

    // 인증코드 전송
    @PostMapping("/send-code")
    public ResponseEntity<Map<String, Object>> sendCode(@RequestBody Map<String, String> request) {
        emailService.sendVerificationCode(request.get("email"));
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 인증코드 검증
    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Object>> verifyCode(@RequestBody Map<String, String> request) {
        emailService.verifyCode(request.get("email"), request.get("code"));
        return ResponseEntity.ok(Map.of("success", true));
    }

    // 회원가입
    @PostMapping("/signup")
    public AuthResponse signup(@RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    // 로그인
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}