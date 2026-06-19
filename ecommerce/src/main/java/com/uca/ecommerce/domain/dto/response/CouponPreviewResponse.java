package com.uca.ecommerce.domain.dto.response;

import com.uca.ecommerce.common.Enums.DiscountType;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponPreviewResponse {
    private UUID couponId;
    private String couponCode;
    private DiscountType discountType;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal discountAmount;
    private BigDecimal total;
}
