package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.mappers.ReviewMapper;
import com.uca.ecommerce.domain.dto.request.review.CreateReviewRequest;
import com.uca.ecommerce.domain.dto.request.review.PatchReviewRequest;
import com.uca.ecommerce.domain.dto.request.review.UpdateReviewRequest;
import com.uca.ecommerce.domain.dto.response.ReviewResponse;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.Review;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.exceptions.FieldAlreadyExistsException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.ProductRepository;
import com.uca.ecommerce.repository.ReviewRepository;
import com.uca.ecommerce.repository.UserRepository;
import com.uca.ecommerce.services.ReviewService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public List<ReviewResponse> getAllReviews() {
        return reviewMapper.toDtoList(reviewRepository.findAll());
    }

    @Override
    public ReviewResponse getReviewById(UUID id) {
        return reviewMapper.toDto(reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review not found")));
    }

    @Override
    public List<ReviewResponse> getReviewsByProductId(UUID productId) {
        return reviewMapper.toDtoList(reviewRepository.findByProductId(productId));
    }

    @Override
    public List<ReviewResponse> getReviewsByUserId(UUID userId) {
        return reviewMapper.toDtoList(reviewRepository.findByUserUuid(userId));
    }

    @Override
    public ReviewResponse createReview(CreateReviewRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        ReviewResponse response = reviewMapper.toDto(
                reviewRepository.save(reviewMapper.toEntityCreate(request, product, user))
        );

        calculateProductStats(request.getProductId());
        return response;
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(UpdateReviewRequest request, UUID id) {
        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review not found"));

        ReviewResponse response = reviewMapper.toDto(
                reviewRepository.save(reviewMapper.toEntityUpdate(request, id, existing))
        );

        calculateProductStats(existing.getProduct().getId());
        return response;
    }

    @Override
    @Transactional
    public ReviewResponse patchReview(PatchReviewRequest request, UUID id) {
        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review not found"));

        ReviewResponse response = reviewMapper.toDto(
                reviewRepository.save(reviewMapper.toEntityPatch(request, existing))
        );

        calculateProductStats(existing.getProduct().getId());
        return response;
    }

    @Override
    public ReviewResponse deleteReview(UUID id) {
        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review not found"));

        UUID productId = existing.getProduct().getId();
        ReviewResponse response = reviewMapper.toDto(existing);
        reviewRepository.deleteById(id);

        calculateProductStats(productId);
        return response;
    }

    private void calculateProductStats(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        List<Review> reviews = reviewRepository.findByProductId(productId);

        int count = reviews.size();
        BigDecimal avgRating = reviews.isEmpty() ? null :
                reviews.stream()
                .map(r -> BigDecimal.valueOf(r.getRating()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);

        product.setRating(avgRating);
        product.setReviewCount(count);
        productRepository.save(product);
    }
}