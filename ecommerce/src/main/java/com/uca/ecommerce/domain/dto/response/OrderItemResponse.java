package com.uca.ecommerce.domain.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {
    private UUID id;

    private UUID orderId;

    private UUID productId;
    private String productName;

    private UUID variantId;
    private String variantSize;
    private String variantColorName;

    private UUID sellerId;
    private String sellerStoreName;

    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}