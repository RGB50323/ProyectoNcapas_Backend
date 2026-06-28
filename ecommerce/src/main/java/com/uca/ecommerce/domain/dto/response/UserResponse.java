package com.uca.ecommerce.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private UUID uuid;
    private String role;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
}