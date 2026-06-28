package com.uca.ecommerce.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private LocalDateTime expiresAt;
    private String role;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
}