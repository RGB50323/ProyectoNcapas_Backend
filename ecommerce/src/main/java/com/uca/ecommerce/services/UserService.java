package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.user.UpdateUserRequest;
import com.uca.ecommerce.domain.dto.response.AuthResponse;
import com.uca.ecommerce.domain.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<UserResponse> getAllUsers();
    UserResponse getUserById(UUID id);
    AuthResponse updateUser(UpdateUserRequest request, UUID id);
    UserResponse deleteUser(UUID id);
}