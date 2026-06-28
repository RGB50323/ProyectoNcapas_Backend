package com.uca.ecommerce.domain.dto.request.brand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBrandRequest {

    @NotBlank(message = "Brand name is required")
    @Size(max = 100, message = "Brand name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Brand slug is required")
    @Size(max = 100, message = "Brand slug must not exceed 100 characters")
    private String slug;

    @Size(max = 500, message = "Logo URL must not exceed 500 characters")
    private String logoUrl;
}