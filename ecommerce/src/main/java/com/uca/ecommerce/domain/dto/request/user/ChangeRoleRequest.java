package com.uca.ecommerce.domain.dto.request.user;

import com.uca.ecommerce.common.Enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeRoleRequest {

    @NotNull(message = "Role is required")
    private Role role;
}
