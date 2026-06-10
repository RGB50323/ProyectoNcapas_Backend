package com.uca.ecommerce.domain.dto.request.dropProduct;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateDropProductRequest {

    @NotNull(message = "Drop ID is required")
    private UUID dropId;

    @NotNull(message = "Product ID is required")
    private UUID productId;
}
