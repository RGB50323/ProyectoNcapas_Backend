package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.request.review.CreateReviewRequest;
import com.uca.ecommerce.domain.dto.request.review.PatchReviewRequest;
import com.uca.ecommerce.domain.dto.request.review.UpdateReviewRequest;
import com.uca.ecommerce.domain.dto.response.ReviewResponse;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.Review;
import com.uca.ecommerce.domain.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReviewMapper {

    public Review toEntityCreate(CreateReviewRequest request, Product product, User user) {
        return Review.builder()
                .product(product)
                .user(user)
                .rating(request.getRating())
                .body(request.getBody())
                .isVerifiedPurchase(request.getIsVerifiedPurchase())
                .build();
    }

    public Review toEntityUpdate(UpdateReviewRequest request, UUID id, Review existing) {
        return Review.builder()
                .id(id)
                .product(existing.getProduct())
                .user(existing.getUser())
                .rating(request.getRating())
                .body(request.getBody())
                .isVerifiedPurchase(existing.getIsVerifiedPurchase())
                .build();
    }

    public Review toEntityPatch(PatchReviewRequest request, Review existing) {
        return Review.builder()
                .id(existing.getId())
                .product(existing.getProduct())
                .user(existing.getUser())
                .rating(request.getRating() != null ? request.getRating() : existing.getRating())
                .body(request.getBody() != null ? request.getBody() : existing.getBody())
                .isVerifiedPurchase(existing.getIsVerifiedPurchase())
                .build();
    }

    public ReviewResponse toDto(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .userId(review.getUser().getUuid())
                .userFirstName(review.getUser().getFirstName())
                .userLastName(review.getUser().getLastName())
                .rating(review.getRating())
                .body(review.getBody())
                .isVerifiedPurchase(review.getIsVerifiedPurchase())
                .createdAt(review.getCreatedAt())
                .build();
    }

    public List<ReviewResponse> toDtoList(List<Review> reviews) {
        return reviews.stream().map(this::toDto).toList();
    }
}