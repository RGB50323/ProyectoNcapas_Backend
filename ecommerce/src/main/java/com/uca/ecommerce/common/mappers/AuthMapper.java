package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.common.Enums.Tier;
import com.uca.ecommerce.domain.dto.request.auth.RegisterRequest;
import com.uca.ecommerce.domain.dto.response.AuthResponse;
import com.uca.ecommerce.domain.entities.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public User toEntity(RegisterRequest request, String encodedPassword) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .passwordHash(encodedPassword)
                .phone(request.getPhone())
                .role(request.getRole())
                .build();
    }

    public AuthResponse toDto(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}