package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findTopByEmailOrderByExpiryTimeDesc(String email);
    Optional<PasswordResetToken> findByEmailAndOtpCode(String email, String otpCode);
}
