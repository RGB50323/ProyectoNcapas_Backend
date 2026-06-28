package com.uca.ecommerce.domain.dto.request.order;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {

    @NotNull(message = "Shipping address ID is required")
    private UUID shippingAddressId;

    @NotNull(message = "Shipping method ID is required")
    private UUID shippingMethodId;

    private UUID couponId;

    private String notes;
}
