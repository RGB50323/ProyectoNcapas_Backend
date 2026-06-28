package com.uca.ecommerce.domain.dto.request.orderItem;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderItemRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    private UUID variantId;

    @NotNull(message = "Seller ID is required")
    private UUID sellerId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}