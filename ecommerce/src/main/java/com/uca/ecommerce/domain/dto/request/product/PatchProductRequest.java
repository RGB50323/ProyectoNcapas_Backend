package com.uca.ecommerce.domain.dto.request.product;

import com.uca.ecommerce.common.Enums.AuthStatus;
import com.uca.ecommerce.common.Enums.ProductCondition;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchProductRequest {

    private UUID sellerId;

    private UUID categoryId;

    private UUID brandId;

    @Size(min = 1, max = 50, message = "Product SKU must contain between 1 and 50 characters")
    private String sku;

    @Size(min = 1, max = 255, message = "Product name must contain between 1 and 255 characters")
    private String name;

    @Size(min = 1, max = 255, message = "Product slug must contain between 1 and 255 characters")
    private String slug;

    private String description;

    private Boolean removeDescription;

    @DecimalMin(value = "0.01", message = "Product price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Product price must have up to 8 integer digits and 2 decimals")
    private BigDecimal price;

    private ProductCondition condition;

    @DecimalMin(value = "0.0", message = "Condition score must be at least 0.0")
    @DecimalMax(value = "5.0", message = "Condition score must be at most 5.0")
    @Digits(integer = 2, fraction = 1, message = "Condition score must have one decimal")
    private BigDecimal conditionScore;

    private Boolean removeConditionScore;

    private AuthStatus authStatus;

    private Boolean featured;

    private Boolean newProduct;

    private Boolean limited;

    private Boolean privateDrop;

    @Min(value = 0, message = "Total stock must be greater than or equal to 0")
    private Integer totalStock;
}