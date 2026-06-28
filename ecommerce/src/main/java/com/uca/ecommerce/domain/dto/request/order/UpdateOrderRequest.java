package com.uca.ecommerce.domain.dto.request.order;

import com.uca.ecommerce.common.Enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderRequest {

    @NotNull(message = "Shipping address ID is required")
    private UUID shippingAddressId;

    @NotNull(message = "Shipping method ID is required")
    private UUID shippingMethodId;

    private UUID couponId;

    @NotNull(message = "Status is required")
    private OrderStatus status;

    private String trackingNumber;

    private String notes;
}