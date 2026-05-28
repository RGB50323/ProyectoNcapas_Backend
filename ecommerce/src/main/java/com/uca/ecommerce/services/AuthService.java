package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.auth.LoginRequest;
import com.uca.ecommerce.domain.dto.request.auth.RegisterRequest;
import com.uca.ecommerce.domain.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}