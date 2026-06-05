package com.uca.ecommerce.domain.dto.request.productImage;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchProductImageRequest {

    private UUID productId;

    @Size(min = 1, max = 500, message = "Image URL must contain between 1 and 500 characters")
    private String url;

    @Size(max = 255, message = "Alt text must not exceed 255 characters")
    private String altText;

    private Boolean removeAltText;

    private Boolean primaryImage;

    @Min(value = 0, message = "Sort order must be greater than or equal to 0")
    private Integer sortOrder;
}