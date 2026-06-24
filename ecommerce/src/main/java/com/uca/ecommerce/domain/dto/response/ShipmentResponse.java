package com.uca.ecommerce.domain.dto.response;

import com.uca.ecommerce.common.Enums.OrderStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentResponse {
    private UUID orderId;
    private String trackingNumber;
    private String shippingMethod;
    private OrderStatus orderStatus;
    private LocalDate estimatedDelivery;
}
