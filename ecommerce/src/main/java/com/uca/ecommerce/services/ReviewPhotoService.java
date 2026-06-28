package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.reviewPhoto.CreateReviewPhotoRequest;
import com.uca.ecommerce.domain.dto.request.reviewPhoto.PatchReviewPhotoRequest;
import com.uca.ecommerce.domain.dto.request.reviewPhoto.UpdateReviewPhotoRequest;
import com.uca.ecommerce.domain.dto.response.ReviewPhotoResponse;

import java.util.List;
import java.util.UUID;

public interface ReviewPhotoService {
    List<ReviewPhotoResponse> getAllReviewPhotos();
    ReviewPhotoResponse getReviewPhotoById(UUID id);
    List<ReviewPhotoResponse> getReviewPhotosByReviewId(UUID reviewId);
    ReviewPhotoResponse createReviewPhoto(CreateReviewPhotoRequest request);
    ReviewPhotoResponse updateReviewPhoto(UpdateReviewPhotoRequest request, UUID id);
    ReviewPhotoResponse patchReviewPhoto(PatchReviewPhotoRequest request, UUID id);
    ReviewPhotoResponse deleteReviewPhoto(UUID id);
}