package com.uca.ecommerce.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private UUID id;
    private UUID productId;
    private String productName;
    private UUID userId;
    private String userFirstName;
    private String userLastName;
    private Integer rating;
    private String body;
    private Boolean isVerifiedPurchase;
    private LocalDateTime createdAt;
}