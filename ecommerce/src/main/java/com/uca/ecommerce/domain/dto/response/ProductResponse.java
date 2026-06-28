package com.uca.ecommerce.domain.dto.response;

import com.uca.ecommerce.common.Enums.AuthStatus;
import com.uca.ecommerce.common.Enums.ProductCondition;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private UUID id;

    private UUID sellerId;
    private String sellerStoreName;

    private UUID categoryId;
    private String categoryName;

    private UUID brandId;
    private String brandName;

    private String sku;
    private String name;
    private String slug;
    private String description;
    private BigDecimal price;
    private ProductCondition condition;
    private BigDecimal conditionScore;
    private AuthStatus authStatus;
    private Boolean featured;
    private Boolean newProduct;
    private Boolean limited;
    private Boolean privateDrop;
    private Integer totalStock;
    private BigDecimal rating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}