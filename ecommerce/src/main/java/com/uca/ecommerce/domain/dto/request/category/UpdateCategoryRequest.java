package com.uca.ecommerce.domain.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCategoryRequest {

    @NotBlank(message="Category name is required")
    private String name;

    @PositiveOrZero(message = "Units must be zero or greater")
    private Integer units;

    private UUID parentId;
}