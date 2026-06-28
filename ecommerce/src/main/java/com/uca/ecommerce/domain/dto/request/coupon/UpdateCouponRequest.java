package com.uca.ecommerce.domain.dto.request.coupon;

import com.uca.ecommerce.common.Enums.DiscountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCouponRequest {

    @NotBlank(message = "Coupon code is required")
    @Size(max = 50, message = "Coupon code must not exceed 50 characters")
    private String code;

    @NotBlank(message = "Coupon label is required")
    @Size(max = 255, message = "Coupon label must not exceed 255 characters")
    private String label;

    @NotNull(message = "Discount type is required")
    private DiscountType type;

    @NotNull(message = "Coupon value is required")
    @DecimalMin(value = "0.0", message = "Coupon value must be greater than or equal to 0")
    @Digits(integer = 8, fraction = 2, message = "Coupon value must have up to 8 integer digits and 2 decimals")
    private BigDecimal value;

    @DecimalMin(value = "0.0", message = "Minimum order amount must be greater than or equal to 0")
    @Digits(integer = 8, fraction = 2, message = "Minimum order amount must have up to 8 integer digits and 2 decimals")
    private BigDecimal minOrderAmount;

    @Min(value = 1, message = "Max uses must be at least 1")
    private Integer maxUses;

    @NotNull(message = "Active flag is required")
    private Boolean active;

    private LocalDateTime expiresAt;
}
