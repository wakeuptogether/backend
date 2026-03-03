package com.example.wakeup_back.domain.user.service;

import com.example.wakeup_back.domain.user.dto.LoginRequest;
import com.example.wakeup_back.domain.user.dto.AuthResponse;
import com.example.wakeup_back.domain.user.dto.SignupRequest;
import com.example.wakeup_back.domain.user.entity.EmailVerification;
import com.example.wakeup_back.domain.user.entity.User;
import com.example.wakeup_back.domain.user.repository.EmailVerificationRepository;
import com.example.wakeup_back.domain.user.repository.UserRepository;
import com.example.wakeup_back.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public void signup(SignupRequest request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
                });

        EmailVerification verification = emailVerificationRepository
                .findTopByEmailOrderByIdDesc(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 인증을 먼저 완료해 주세요."));

        if (!verification.isVerified()) {
            throw new IllegalArgumentException("이메일 인증이 완료되지 않았습니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt 암호화
                .name(request.getName())
                .build();

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String token = jwtTokenProvider.createToken(user.getId(), user.getEmail());

        return AuthResponse.builder()
                .userId(user.getId())
                .token(token)
                .name(user.getName())
                .email(user.getEmail())
                .build();

    }
}