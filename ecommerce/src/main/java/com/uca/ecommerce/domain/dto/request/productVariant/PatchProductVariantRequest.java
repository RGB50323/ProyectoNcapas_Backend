package com.uca.ecommerce.domain.dto.request.productVariant;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchProductVariantRequest {

    private UUID productId;

    @Size(min = 1, max = 20, message = "Size must contain between 1 and 20 characters")
    private String size;

    @Size(min = 1, max = 100, message = "Color name must contain between 1 and 100 characters")
    private String colorName;

    @Pattern(
            regexp = "^#[0-9A-Fa-f]{6}$",
            message = "Color hex must be a valid hexadecimal color, example: #FFFFFF"
    )
    private String colorHex;

    @Min(value = 0, message = "Stock must be greater than or equal to 0")
    private Integer stock;

    @Digits(integer = 8, fraction = 2, message = "Price delta must have up to 8 integer digits and 2 decimals")
    private BigDecimal priceDelta;
}