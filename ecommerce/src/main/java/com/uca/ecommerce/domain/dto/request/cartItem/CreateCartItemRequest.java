package com.uca.ecommerce.domain.dto.request.cartItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCartItemRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Variant ID is required")
    private UUID variantId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
