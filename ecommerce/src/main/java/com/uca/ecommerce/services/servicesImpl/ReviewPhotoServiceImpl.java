package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.mappers.ReviewPhotoMapper;
import com.uca.ecommerce.domain.dto.request.reviewPhoto.CreateReviewPhotoRequest;
import com.uca.ecommerce.domain.dto.request.reviewPhoto.PatchReviewPhotoRequest;
import com.uca.ecommerce.domain.dto.request.reviewPhoto.UpdateReviewPhotoRequest;
import com.uca.ecommerce.domain.dto.response.ReviewPhotoResponse;
import com.uca.ecommerce.domain.entities.Review;
import com.uca.ecommerce.domain.entities.ReviewPhoto;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.ReviewPhotoRepository;
import com.uca.ecommerce.repository.ReviewRepository;
import com.uca.ecommerce.services.ReviewPhotoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewPhotoServiceImpl implements ReviewPhotoService {

    private final ReviewPhotoRepository reviewPhotoRepository;
    private final ReviewPhotoMapper reviewPhotoMapper;
    private final ReviewRepository reviewRepository;

    @Override
    public List<ReviewPhotoResponse> getAllReviewPhotos() {
        return reviewPhotoMapper.toDtoList(reviewPhotoRepository.findAll());
    }

    @Override
    public ReviewPhotoResponse getReviewPhotoById(UUID id) {
        return reviewPhotoMapper.toDto(reviewPhotoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review photo not found")));
    }

    @Override
    public List<ReviewPhotoResponse> getReviewPhotosByReviewId(UUID reviewId) {
        return reviewPhotoMapper.toDtoList(reviewPhotoRepository.findByReviewId(reviewId));
    }

    @Override
    public ReviewPhotoResponse createReviewPhoto(CreateReviewPhotoRequest request) {
        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new NotFoundException("Review not found"));

        return reviewPhotoMapper.toDto(
                reviewPhotoRepository.save(reviewPhotoMapper.toEntityCreate(request, review))
        );
    }

    @Override
    @Transactional
    public ReviewPhotoResponse updateReviewPhoto(UpdateReviewPhotoRequest request, UUID id) {
        ReviewPhoto existing = reviewPhotoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review photo not found"));

        return reviewPhotoMapper.toDto(
                reviewPhotoRepository.save(reviewPhotoMapper.toEntityUpdate(request, id, existing.getReview()))
        );
    }

    @Override
    @Transactional
    public ReviewPhotoResponse patchReviewPhoto(PatchReviewPhotoRequest request, UUID id) {
        ReviewPhoto existing = reviewPhotoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review photo not found"));

        return reviewPhotoMapper.toDto(
                reviewPhotoRepository.save(reviewPhotoMapper.toEntityPatch(request, existing))
        );
    }

    @Override
    public ReviewPhotoResponse deleteReviewPhoto(UUID id) {
        ReviewPhotoResponse existing = this.getReviewPhotoById(id);
        reviewPhotoRepository.deleteById(id);
        return existing;
    }
}