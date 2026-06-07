package com.uca.ecommerce.domain.dto.request.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

    @NotBlank(message = "Firstname is required")
    private String firstName;

    @NotBlank(message = "Lastname is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Invalid phone format")
    private String phone;
}
