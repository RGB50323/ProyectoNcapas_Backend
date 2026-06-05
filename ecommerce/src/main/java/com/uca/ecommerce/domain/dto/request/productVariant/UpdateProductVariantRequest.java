package com.uca.ecommerce.domain.dto.request.productVariant;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductVariantRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotBlank(message = "Size is required")
    @Size(max = 20, message = "Size must not exceed 20 characters")
    private String size;

    @NotBlank(message = "Color name is required")
    @Size(max = 100, message = "Color name must not exceed 100 characters")
    private String colorName;

    @NotBlank(message = "Color hex is required")
    @Pattern(
            regexp = "^#[0-9A-Fa-f]{6}$",
            message = "Color hex must be a valid hexadecimal color, example: #FFFFFF"
    )
    private String colorHex;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must be greater than or equal to 0")
    private Integer stock;

    @NotNull(message = "Price delta is required")
    @Digits(integer = 8, fraction = 2, message = "Price delta must have up to 8 integer digits and 2 decimals")
    private BigDecimal priceDelta;
}