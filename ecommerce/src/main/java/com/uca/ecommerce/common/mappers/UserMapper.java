package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.request.user.UpdateUserRequest;
import com.uca.ecommerce.domain.dto.response.UserResponse;
import com.uca.ecommerce.domain.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public User toEntityUpdate(UpdateUserRequest request, UUID id, User existing) {
        return User.builder()
                .uuid(id)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(existing.getRole())
                .passwordHash(existing.getPasswordHash())
                .tier(existing.getTier())
                .active(existing.isActive())
                .createdAt(existing.getCreatedAt())
                .build();
    }

    public UserResponse toDto(User user) {
        return UserResponse.builder()
                .uuid(user.getUuid())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .build();
    }

    public List<UserResponse> toDtoList(List<User> users) {
        return users.stream().map(this::toDto).toList();
    }
}