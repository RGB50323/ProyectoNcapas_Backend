package com.uca.ecommerce.domain.dto.request.category;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchCategoryRequest {
    private String name;

    @PositiveOrZero(message = "Units must be zero or greater")
    private Integer units;

    private UUID parentId;

    private Boolean removeParent;
}
