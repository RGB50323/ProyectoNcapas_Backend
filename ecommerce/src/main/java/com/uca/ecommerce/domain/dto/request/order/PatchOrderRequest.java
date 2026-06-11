package com.uca.ecommerce.domain.dto.request.order;

import com.uca.ecommerce.common.Enums.OrderStatus;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchOrderRequest {

    private UUID shippingAddressId;

    private UUID shippingMethodId;

    private UUID couponId;
    private Boolean removeCoupon;

    private OrderStatus status;

    private String trackingNumber;
    private Boolean removeTrackingNumber;

    private String notes;
    private Boolean removeNotes;
}