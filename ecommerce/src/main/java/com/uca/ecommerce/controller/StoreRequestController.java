package com.uca.ecommerce.controller;

import com.uca.ecommerce.common.Enums.StoreCategory;
import com.uca.ecommerce.domain.dto.request.storeRequest.CreateStoreRequestRequest;
import com.uca.ecommerce.domain.dto.request.storeRequest.ReviewStoreRequestRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.StoreRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.UUID;

@RestController
@RequestMapping("/store_requests")
@RequiredArgsConstructor
public class StoreRequestController extends BaseController {

    private final StoreRequestService storeRequestService;

    @GetMapping("/store-categories")
    public ResponseEntity<GeneralResponse> getStoreCategories() {
        return buildResponse("Store categories retrieved successfully", HttpStatus.OK,
                Arrays.stream(StoreCategory.values()).map(Enum::name).toList());
    }

    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> create(@Valid @RequestBody CreateStoreRequestRequest request) {
        return buildResponse("Store request submitted successfully", HttpStatus.CREATED, storeRequestService.create(request));
    }

    @GetMapping("/me")
    public ResponseEntity<GeneralResponse> getMyLatest() {
        return buildResponse("Store request retrieved successfully", HttpStatus.OK, storeRequestService.getMyLatest());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAll() {
        return buildResponse("Store requests retrieved successfully", HttpStatus.OK, storeRequestService.getAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<GeneralResponse> getPending() {
        return buildResponse("Pending store requests retrieved successfully", HttpStatus.OK, storeRequestService.getPending());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/review")
    public ResponseEntity<GeneralResponse> review(@PathVariable UUID id, @Valid @RequestBody ReviewStoreRequestRequest request) {
        return buildResponse("Store request reviewed successfully", HttpStatus.OK, storeRequestService.review(id, request));
    }
}
