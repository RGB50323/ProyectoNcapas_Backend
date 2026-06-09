package com.uca.ecommerce.domain.dto.request.coupon;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreviewCouponRequest {

    @NotBlank(message = "Coupon code is required")
    private String code;

    // Optional: used to compute the FREE_SHIPPING discount against a chosen shipping method
    private UUID shippingMethodId;
}
