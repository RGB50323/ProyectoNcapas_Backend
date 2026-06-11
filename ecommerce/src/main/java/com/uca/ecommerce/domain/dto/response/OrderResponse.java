package com.uca.ecommerce.domain.dto.response;

import com.uca.ecommerce.common.Enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private UUID id;

    private UUID customerId;
    private String customerFullName;

    private UUID shippingAddressId;
    private String shippingAddressStreet;
    private String shippingAddressCity;
    private String shippingAddressCountry;

    private UUID shippingMethodId;
    private String shippingMethodName;

    private UUID couponId;
    private String couponCode;

    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal discountAmount;
    private BigDecimal total;
    private String trackingNumber;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}