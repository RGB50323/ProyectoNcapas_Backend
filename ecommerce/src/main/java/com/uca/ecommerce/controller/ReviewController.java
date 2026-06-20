package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.request.review.CreateReviewRequest;
import com.uca.ecommerce.domain.dto.request.review.PatchReviewRequest;
import com.uca.ecommerce.domain.dto.request.review.UpdateReviewRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController extends BaseController {

    private final ReviewService reviewService;

    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllReviews() {
        return buildResponse("Reviews retrieved successfully", HttpStatus.OK, reviewService.getAllReviews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getReviewById(@PathVariable UUID id) {
        return buildResponse("Review retrieved successfully", HttpStatus.OK, reviewService.getReviewById(id));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<GeneralResponse> getReviewsByProductId(@PathVariable UUID productId) {
        return buildResponse("Reviews retrieved successfully", HttpStatus.OK, reviewService.getReviewsByProductId(productId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<GeneralResponse> getReviewsByUserId(@PathVariable UUID userId) {
        return buildResponse("Reviews retrieved successfully", HttpStatus.OK, reviewService.getReviewsByUserId(userId));
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<GeneralResponse> getReviewsBySellerId(@PathVariable UUID sellerId) {
        return buildResponse("Reviews retrieved successfully", HttpStatus.OK, reviewService.getReviewsBySellerId(sellerId));
    }

    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createReview(@Valid @RequestBody CreateReviewRequest request) {
        return buildResponse("Review created successfully", HttpStatus.CREATED, reviewService.createReview(request));
    }

    @PreAuthorize("hasRole('BUYER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<GeneralResponse> updateReview(@Valid @RequestBody UpdateReviewRequest request, @PathVariable UUID id) {
        return buildResponse("Review updated successfully", HttpStatus.OK, reviewService.updateReview(request, id));
    }

    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/patch/{id}")
    public ResponseEntity<GeneralResponse> patchReview(@Valid @RequestBody PatchReviewRequest request, @PathVariable UUID id) {
        return buildResponse("Review patched successfully", HttpStatus.OK, reviewService.patchReview(request, id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','BUYER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteReview(@PathVariable UUID id) {
        return buildResponse("Review deleted successfully along with its associated photos", HttpStatus.OK, reviewService.deleteReview(id));
    }
}
