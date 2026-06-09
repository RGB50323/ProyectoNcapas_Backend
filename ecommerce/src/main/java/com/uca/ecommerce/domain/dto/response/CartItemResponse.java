package com.uca.ecommerce.domain.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {

    private UUID id;

    private UUID userId;

    private UUID productId;
    private String productName;
    private String productSku;

    private UUID variantId;
    private String variantSize;
    private String variantColorName;
    private String variantColorHex;

    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    private LocalDateTime addedAt;
}
