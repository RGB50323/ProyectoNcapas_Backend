package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.entities.RefreshToken;
import com.uca.ecommerce.domain.entities.User;

public interface RefreshTokenService {
    RefreshToken create(User user);
    RefreshToken validate(String token);
    void revoke(String token);
    void revokeAll(User user);
}