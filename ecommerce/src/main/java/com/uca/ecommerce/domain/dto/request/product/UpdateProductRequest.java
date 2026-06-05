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
public class UpdateProductRequest {

    @NotNull(message = "Seller ID is required")
    private UUID sellerId;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    @NotNull(message = "Brand ID is required")
    private UUID brandId;

    @NotBlank(message = "Product SKU is required")
    @Size(max = 50, message = "Product SKU must not exceed 50 characters")
    private String sku;

    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Product slug is required")
    @Size(max = 255, message = "Product slug must not exceed 255 characters")
    private String slug;

    private String description;

    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.01", message = "Product price must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Product price must have up to 8 integer digits and 2 decimals")
    private BigDecimal price;

    @NotNull(message = "Product condition is required")
    private ProductCondition condition;

    @DecimalMin(value = "0.0", message = "Condition score must be at least 0.0")
    @DecimalMax(value = "5.0", message = "Condition score must be at most 5.0")
    @Digits(integer = 2, fraction = 1, message = "Condition score must have one decimal")
    private BigDecimal conditionScore;

    @NotNull(message = "Authentication status is required")
    private AuthStatus authStatus;

    @NotNull(message = "Featured status is required")
    private Boolean featured;

    @NotNull(message = "New product status is required")
    private Boolean newProduct;

    @NotNull(message = "Limited status is required")
    private Boolean limited;

    @NotNull(message = "Private drop status is required")
    private Boolean privateDrop;

    @NotNull(message = "Total stock is required")
    @Min(value = 0, message = "Total stock must be greater than or equal to 0")
    private Integer totalStock;
}