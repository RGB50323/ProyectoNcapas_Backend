package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.request.reviewPhoto.CreateReviewPhotoRequest;
import com.uca.ecommerce.domain.dto.request.reviewPhoto.PatchReviewPhotoRequest;
import com.uca.ecommerce.domain.dto.request.reviewPhoto.UpdateReviewPhotoRequest;
import com.uca.ecommerce.domain.dto.response.ReviewPhotoResponse;
import com.uca.ecommerce.domain.entities.Review;
import com.uca.ecommerce.domain.entities.ReviewPhoto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReviewPhotoMapper {

    public ReviewPhoto toEntityCreate(CreateReviewPhotoRequest request, Review review) {
        return ReviewPhoto.builder()
                .review(review)
                .url(request.getUrl())
                .sortOrder(request.getSortOrder())
                .build();
    }

    public ReviewPhoto toEntityUpdate(UpdateReviewPhotoRequest request, UUID id, Review review) {
        return ReviewPhoto.builder()
                .id(id)
                .review(review)
                .url(request.getUrl())
                .sortOrder(request.getSortOrder())
                .build();
    }

    public ReviewPhoto toEntityPatch(PatchReviewPhotoRequest request, ReviewPhoto existing) {
        return ReviewPhoto.builder()
                .id(existing.getId())
                .review(existing.getReview())
                .url(request.getUrl() != null ? request.getUrl() : existing.getUrl())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : existing.getSortOrder())
                .build();
    }

    public ReviewPhotoResponse toDto(ReviewPhoto reviewPhoto) {
        return ReviewPhotoResponse.builder()
                .id(reviewPhoto.getId())
                .reviewId(reviewPhoto.getReview().getId())
                .url(reviewPhoto.getUrl())
                .sortOrder(reviewPhoto.getSortOrder())
                .build();
    }

    public List<ReviewPhotoResponse> toDtoList(List<ReviewPhoto> reviewPhotos) {
        return reviewPhotos.stream().map(this::toDto).toList();
    }
}