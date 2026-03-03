package com.example.wakeup_back.domain.user.service;

import com.example.wakeup_back.domain.user.entity.EmailVerification;
import com.example.wakeup_back.domain.user.repository.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository emailVerificationRepository;

    public void sendVerificationCode(String email) {
        String code = String.format("%06d", new Random().nextInt(1000000));

        EmailVerification verification = EmailVerification.builder()
                .email(email)
                .code(code)
                .expireAt(LocalDateTime.now().plusMinutes(5))
                .verified(false)
                .build();

        emailVerificationRepository.save(verification);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[WakeUp] 이메일 인증 코드");
            message.setText("인증 코드: " + code + "\n\n5분 안에 입력해 주세요.");
            mailSender.send(message);
            System.out.println("이메일 전송 성공: " + email);
        } catch (Exception e) {
            System.out.println("이메일 전송 실패 원인: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void verifyCode(String email, String code) {
        EmailVerification verification = emailVerificationRepository
                .findTopByEmailOrderByIdDesc(email)
                .orElseThrow(() -> new IllegalArgumentException("인증 코드를 먼저 요청해 주세요."));

        if (verification.isExpired()) {
            throw new IllegalArgumentException("인증 코드가 만료되었습니다.");
        }

        if (!verification.getCode().equals(code)) {
            throw new IllegalArgumentException("인증 코드가 올바르지 않습니다.");
        }

        verification.verify();
        emailVerificationRepository.save(verification);
    }
}