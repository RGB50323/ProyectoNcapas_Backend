package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.request.user.ChangeRoleRequest;
import com.uca.ecommerce.domain.dto.request.user.UpdateUserRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController extends BaseController {

    private final UserService userService;

    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllUsers() {
        return buildResponse("Users retrieved successfully", HttpStatus.OK, userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getUserById(@PathVariable UUID id) {
        return buildResponse("User retrieved successfully", HttpStatus.OK, userService.getUserById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GeneralResponse> updateUser(@Valid @RequestBody UpdateUserRequest request, @PathVariable UUID id) {
        return buildResponse("User updated successfully, use the new token", HttpStatus.OK, userService.updateUser(request, id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/role")
    public ResponseEntity<GeneralResponse> changeUserRole(@Valid @RequestBody ChangeRoleRequest request, @PathVariable UUID id) {
        return buildResponse("User role updated successfully", HttpStatus.OK, userService.changeUserRole(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteUser(@PathVariable UUID id) {
        return buildResponse("User deleted successfully", HttpStatus.OK, userService.deleteUser(id));
    }
}