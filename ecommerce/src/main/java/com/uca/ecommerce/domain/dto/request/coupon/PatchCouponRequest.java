package com.uca.ecommerce.domain.dto.request.coupon;

import com.uca.ecommerce.common.Enums.DiscountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchCouponRequest {

    @Size(min = 1, max = 50, message = "Coupon code must contain between 1 and 50 characters")
    private String code;

    @Size(min = 1, max = 255, message = "Coupon label must contain between 1 and 255 characters")
    private String label;

    private DiscountType type;

    @DecimalMin(value = "0.0", message = "Coupon value must be greater than or equal to 0")
    @Digits(integer = 8, fraction = 2, message = "Coupon value must have up to 8 integer digits and 2 decimals")
    private BigDecimal value;

    @DecimalMin(value = "0.0", message = "Minimum order amount must be greater than or equal to 0")
    @Digits(integer = 8, fraction = 2, message = "Minimum order amount must have up to 8 integer digits and 2 decimals")
    private BigDecimal minOrderAmount;
    private Boolean removeMinOrderAmount;

    @Min(value = 1, message = "Max uses must be at least 1")
    private Integer maxUses;
    private Boolean removeMaxUses;

    private Boolean active;

    private LocalDateTime expiresAt;
    private Boolean removeExpiresAt;
}
