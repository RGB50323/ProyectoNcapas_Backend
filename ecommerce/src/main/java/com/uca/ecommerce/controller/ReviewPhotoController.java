package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.request.reviewPhoto.CreateReviewPhotoRequest;
import com.uca.ecommerce.domain.dto.request.reviewPhoto.PatchReviewPhotoRequest;
import com.uca.ecommerce.domain.dto.request.reviewPhoto.UpdateReviewPhotoRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.FileStorageService;
import com.uca.ecommerce.services.ReviewPhotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/review-photos")
@RequiredArgsConstructor
public class ReviewPhotoController extends BaseController {

    private final ReviewPhotoService reviewPhotoService;
    private final FileStorageService fileStorageService;

    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllReviewPhotos() {
        return buildResponse("Review photos retrieved successfully", HttpStatus.OK, reviewPhotoService.getAllReviewPhotos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getReviewPhotoById(@PathVariable UUID id) {
        return buildResponse("Review photo retrieved successfully", HttpStatus.OK, reviewPhotoService.getReviewPhotoById(id));
    }

    @GetMapping("/review/{reviewId}")
    public ResponseEntity<GeneralResponse> getReviewPhotosByReviewId(@PathVariable UUID reviewId) {
        return buildResponse("Review photos retrieved successfully", HttpStatus.OK, reviewPhotoService.getReviewPhotosByReviewId(reviewId));
    }

    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createReviewPhoto(@Valid @RequestBody CreateReviewPhotoRequest request) {
        return buildResponse("Review photo created successfully", HttpStatus.CREATED, reviewPhotoService.createReviewPhoto(request));
    }

    @PreAuthorize("hasRole('BUYER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<GeneralResponse> updateReviewPhoto(@Valid @RequestBody UpdateReviewPhotoRequest request, @PathVariable UUID id) {
        return buildResponse("Review photo updated successfully", HttpStatus.OK, reviewPhotoService.updateReviewPhoto(request, id));
    }

    @PreAuthorize("hasRole('BUYER')")
    @PatchMapping("/patch/{id}")
    public ResponseEntity<GeneralResponse> patchReviewPhoto(@Valid @RequestBody PatchReviewPhotoRequest request, @PathVariable UUID id) {
        return buildResponse("Review photo patched successfully", HttpStatus.OK, reviewPhotoService.patchReviewPhoto(request, id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','BUYER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteReviewPhoto(@PathVariable UUID id) {
        return buildResponse("Review photo deleted successfully", HttpStatus.OK, reviewPhotoService.deleteReviewPhoto(id));
    }

    @PreAuthorize("hasRole('BUYER')")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeneralResponse> uploadReviewPhoto(@RequestParam("file") MultipartFile file) {
        String relative = fileStorageService.store(file, "reviews");
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(relative)
                .toUriString();
        return buildResponse("Review photo uploaded successfully", HttpStatus.CREATED, Map.of("url", url));
    }
}