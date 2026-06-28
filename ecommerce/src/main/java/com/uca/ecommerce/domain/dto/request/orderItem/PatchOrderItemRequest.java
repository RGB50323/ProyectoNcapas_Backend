package com.uca.ecommerce.domain.dto.request.orderItem;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchOrderItemRequest {

    private UUID variantId;
    private Boolean removeVariant;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}