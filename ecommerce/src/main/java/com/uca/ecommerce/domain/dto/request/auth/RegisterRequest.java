package com.uca.ecommerce.domain.dto.request.auth;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Firstname is required")
    @Size(min = 3, max = 500, message = "Firstname must be between 3 and 500 characters")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ '-]+$", message = "Firstname must contain only letters")
    private String firstName;

    @NotBlank(message = "Lastname is required")
    @Size(min = 3, max = 500, message = "Lastname must be between 3 and 500 characters")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ '-]+$", message = "Lastname must contain only letters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least one uppercase, one lowercase, one number and one special character"
    )
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Invalid phone format")
    private String phone;

    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordsMatch() {
        return password != null && password.equals(confirmPassword);
    }
}
