package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.mappers.AuthMapper;
import com.uca.ecommerce.common.mappers.UserMapper;
import com.uca.ecommerce.domain.dto.request.user.ChangeRoleRequest;
import com.uca.ecommerce.domain.dto.request.user.UpdateUserRequest;
import com.uca.ecommerce.domain.dto.response.AuthResponse;
import com.uca.ecommerce.domain.dto.response.UserResponse;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.exceptions.FieldAlreadyExistsException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.UserRepository;
import com.uca.ecommerce.security.JwtService;
import com.uca.ecommerce.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthMapper authMapper;
    private final JwtService jwtService;

    @Override
    public List<UserResponse> getAllUsers() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    @Override
    public UserResponse getUserById(UUID id) {
        return userMapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found")));
    }

    @Override
    @Transactional
    public AuthResponse updateUser(UpdateUserRequest request, UUID id) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!existing.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail()))
            throw new FieldAlreadyExistsException("Email already exists");

        if (request.getPhone() != null
                && !request.getPhone().equals(existing.getPhone())
                && userRepository.existsByPhone(request.getPhone()))
            throw new FieldAlreadyExistsException("Phone already exists");

        User updated = userRepository.save(userMapper.toEntityUpdate(request, id, existing));
        String token = jwtService.generateToken(updated.getUuid(), updated.getEmail(), updated.getRole().name());
        return authMapper.toDto(updated, token);
    }


    @Override
    public UserResponse changeUserRole(ChangeRoleRequest request, UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setRole(request.getRole());
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponse deleteUser(UUID id) {
        UserResponse existing = this.getUserById(id);
        userRepository.deleteById(id);
        return existing;
    }
}