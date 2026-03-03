package com.example.wakeup_back.domain.user.repository;

import com.example.wakeup_back.domain.user.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findTopByEmailOrderByIdDesc(String email);
}