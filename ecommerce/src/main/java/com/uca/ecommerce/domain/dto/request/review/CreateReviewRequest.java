package com.uca.ecommerce.domain.dto.request.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class CreateReviewRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @NotNull(message = "Body is required")
    @Size(min = 10, max = 2000, message = "Body must not exceed 2000 characters")
    private String body;

    @NotNull(message = "Verified purchase status is required")
    private Boolean isVerifiedPurchase;
}