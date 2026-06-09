package com.uca.ecommerce.domain.dto.response;

import com.uca.ecommerce.common.Enums.DiscountType;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponPreviewResponse {
    private String couponCode;
    private DiscountType discountType;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal discountAmount;
    private BigDecimal total;
}
