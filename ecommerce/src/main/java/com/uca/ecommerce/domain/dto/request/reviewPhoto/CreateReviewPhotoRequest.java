package com.uca.ecommerce.domain.dto.request.reviewPhoto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateReviewPhotoRequest {

    @NotNull(message = "Review ID is required")
    private UUID reviewId;

    @NotBlank(message = "URL is required")
    @Size(max = 500, message = "URL must not exceed 500 characters")
    private String url;

    @Min(value = 0, message = "Sort order must be greater than or equal to 0")
    private Integer sortOrder;
}
