package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.entities.PasswordResetToken;
import com.uca.ecommerce.domain.entities.User;

public interface PasswordResetTokenService {
    PasswordResetToken create(User user);
    PasswordResetToken validate(String token);
    void markAsUsed(String token);
}