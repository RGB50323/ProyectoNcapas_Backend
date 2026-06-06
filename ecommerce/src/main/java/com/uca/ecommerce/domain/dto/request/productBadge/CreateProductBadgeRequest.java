package com.uca.ecommerce.domain.dto.request.productBadge;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductBadgeRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotBlank(message = "Badge label is required")
    @Size(max = 50, message = "Badge label must not exceed 50 characters")
    private String label;
}