package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.domain.entities.PasswordResetToken;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.exceptions.InvalidCredentialsException;
import com.uca.ecommerce.repository.PasswordResetTokenRepository;
import com.uca.ecommerce.services.PasswordResetTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    @Transactional
    public PasswordResetToken create(User user) {
        return passwordResetTokenRepository.save(
                PasswordResetToken.builder()
                        .token(UUID.randomUUID().toString())
                        .user(user)
                        .expiresAt(LocalDateTime.now().plusMinutes(15))
                        .build()
        );
    }

    @Override
    public PasswordResetToken validate(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid reset token"));

        if (resetToken.isUsed()) {
            throw new InvalidCredentialsException("Reset token has already been used");
        }

        if (resetToken.isExpired()) {
            throw new InvalidCredentialsException("Reset token has expired");
        }

        return resetToken;
    }

    @Override
    @Transactional
    public void markAsUsed(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid reset token"));
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
}