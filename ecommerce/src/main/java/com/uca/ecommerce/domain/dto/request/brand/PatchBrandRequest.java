package com.uca.ecommerce.domain.dto.request.brand;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchBrandRequest {

    @Size(min = 1, max = 100, message = "Brand name must contain between 1 and 100 characters")
    private String name;

    @Size(min = 1, max = 100, message = "Brand slug must contain between 1 and 100 characters")
    private String slug;

    @Size(max = 500, message = "Logo URL must not exceed 500 characters")
    private String logoUrl;

    private Boolean removeLogo;
}
