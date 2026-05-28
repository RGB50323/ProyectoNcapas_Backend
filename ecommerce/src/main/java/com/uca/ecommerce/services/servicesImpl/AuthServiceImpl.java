package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.mappers.AuthMapper;
import com.uca.ecommerce.domain.dto.request.auth.LoginRequest;
import com.uca.ecommerce.domain.dto.request.auth.RegisterRequest;
import com.uca.ecommerce.domain.dto.response.AuthResponse;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.exceptions.EmailAlreadyExistsException;
import com.uca.ecommerce.exceptions.InvalidCredentialsException;
import com.uca.ecommerce.repository.UserRepository;
import com.uca.ecommerce.security.JwtService;
import com.uca.ecommerce.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthMapper authMapper;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered: " + request.getEmail());
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = authMapper.toEntity(request, encodedPassword);
        userRepository.save(user);

        String token = jwtService.generateToken(user.getUuid(), user.getEmail(), user.getRole().name());
        return authMapper.toDto(user, token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getUuid(), user.getEmail(), user.getRole().name());
        return authMapper.toDto(user, token);
    }
}