package com.uca.ecommerce.domain.dto.request.productImage;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductImageRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotBlank(message = "Image URL is required")
    @Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String url;

    @Size(max = 255, message = "Alt text must not exceed 255 characters")
    private String altText;

    @NotNull(message = "Primary image status is required")
    private Boolean primaryImage;

    @NotNull(message = "Sort order is required")
    @Min(value = 0, message = "Sort order must be greater than or equal to 0")
    private Integer sortOrder;
}