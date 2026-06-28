package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.review.CreateReviewRequest;
import com.uca.ecommerce.domain.dto.request.review.PatchReviewRequest;
import com.uca.ecommerce.domain.dto.request.review.UpdateReviewRequest;
import com.uca.ecommerce.domain.dto.response.ReviewResponse;
import com.uca.ecommerce.domain.dto.response.ReviewableProductResponse;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    List<ReviewResponse> getAllReviews();
    ReviewResponse getReviewById(UUID id);
    List<ReviewResponse> getReviewsByProductId(UUID productId);
    List<ReviewResponse> getReviewsByUserId(UUID userId);
    List<ReviewResponse> getReviewsBySellerId(UUID sellerId);
    List<ReviewableProductResponse> getReviewableProducts(UUID userId);
    ReviewResponse createReview(CreateReviewRequest request);
    ReviewResponse updateReview(UpdateReviewRequest request, UUID id);
    ReviewResponse patchReview(PatchReviewRequest request, UUID id);
    ReviewResponse deleteReview(UUID id);
}