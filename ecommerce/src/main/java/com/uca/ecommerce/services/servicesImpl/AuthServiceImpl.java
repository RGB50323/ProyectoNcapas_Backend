package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.mappers.AuthMapper;
import com.uca.ecommerce.domain.dto.request.auth.LoginRequest;
import com.uca.ecommerce.domain.dto.request.auth.RegisterRequest;
import com.uca.ecommerce.domain.dto.response.AuthResponse;
import com.uca.ecommerce.domain.entities.RefreshToken;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.exceptions.FieldAlreadyExistsException;
import com.uca.ecommerce.exceptions.InvalidCredentialsException;
import com.uca.ecommerce.repository.UserRepository;
import com.uca.ecommerce.security.JwtService;
import com.uca.ecommerce.services.AuthService;
import com.uca.ecommerce.services.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthMapper authMapper;
    private final RefreshTokenService refreshTokenService;

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateToken(
                user.getUuid(), user.getEmail(), user.getRole().name()
        );
        RefreshToken refreshToken = refreshTokenService.create(user);
        LocalDateTime expiresAt = jwtService.getExpiresAt(accessToken);

        return authMapper.toDto(user, accessToken, refreshToken.getToken(), expiresAt);
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new FieldAlreadyExistsException("Email already registered: " + request.getEmail());
        }

        if (request.getPhone() != null && !request.getPhone().isBlank()
                && userRepository.existsByPhone(request.getPhone())) {
            throw new FieldAlreadyExistsException("Phone already registered: " + request.getPhone());
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = authMapper.toEntity(request, encodedPassword);
        userRepository.save(user);

        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse refresh(String token) {
        RefreshToken refreshToken = refreshTokenService.validate(token);
        refreshTokenService.revoke(token);
        return buildAuthResponse(refreshToken.getUser());
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    @Override
    @Transactional
    public void logoutAll(String accessToken) {
        String email = jwtService.extractEmail(accessToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
        refreshTokenService.revokeAll(user);
    }
}