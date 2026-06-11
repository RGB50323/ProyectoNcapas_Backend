package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.request.auth.*;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<GeneralResponse> register(@Valid @RequestBody RegisterRequest request) {
        return buildResponse("User registered successfully", HttpStatus.CREATED, authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<GeneralResponse> login(@Valid @RequestBody LoginRequest request) {
        return buildResponse("Login successful", HttpStatus.OK, authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<GeneralResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return buildResponse("Token refreshed successfully", HttpStatus.OK, authService.refresh(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<GeneralResponse> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request.getRefreshToken());
        return buildResponse("Logged out successfully", HttpStatus.OK, null);
    }

    @PostMapping("/logout-all")
    public ResponseEntity<GeneralResponse> logoutAll(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").substring(7);
        authService.logoutAll(accessToken);
        return buildResponse("Logged out from all devices", HttpStatus.OK, null);
    }

    @PutMapping("/change-password")
    public ResponseEntity<GeneralResponse> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest) {
        String accessToken = httpRequest.getHeader("Authorization").substring(7);
        authService.changePassword(request, accessToken);
        return buildResponse("Password changed successfully", HttpStatus.OK, null);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<GeneralResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return buildResponse("Password reset email sent", HttpStatus.OK, null);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<GeneralResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return buildResponse("Password reset successfully", HttpStatus.OK, null);
    }
}