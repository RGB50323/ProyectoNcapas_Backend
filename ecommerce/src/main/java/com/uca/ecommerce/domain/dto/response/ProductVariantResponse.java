package com.uca.ecommerce.domain.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantResponse {

    private UUID id;

    private UUID productId;
    private String productName;
    private String productSku;
    private String productSlug;

    private String size;
    private String colorName;
    private String colorHex;
    private Integer stock;
    private BigDecimal priceDelta;
}