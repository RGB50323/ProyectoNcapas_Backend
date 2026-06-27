package com.uca.ecommerce.domain.dto.response;

import com.uca.ecommerce.common.Enums.DiscountType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponResponse {
    private UUID id;
    private String code;
    private String label;
    private DiscountType type;
    private BigDecimal value;
    private BigDecimal minOrderAmount;
    private Integer maxUses;
    private Integer usesCount;
    private Boolean active;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private UUID ownerId;
}
