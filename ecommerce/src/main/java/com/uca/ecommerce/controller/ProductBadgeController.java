package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.request.productBadge.CreateProductBadgeRequest;
import com.uca.ecommerce.domain.dto.request.productBadge.UpdateProductBadgeRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.ProductBadgeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/product-badges")
@RequiredArgsConstructor
public class ProductBadgeController extends BaseController {

    private final ProductBadgeService badgeService;

    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllBadges() {
        return buildResponse(
                "Badges retrieved successfully",
                HttpStatus.OK,
                badgeService.getAllBadges()
        );
    }

    @GetMapping("/public")
    public ResponseEntity<GeneralResponse> getPublicBadges() {
        return buildResponse(
                "Public badges retrieved successfully",
                HttpStatus.OK,
                badgeService.getPublicBadges()
        );
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<GeneralResponse> getBadgesByProduct(@PathVariable UUID productId) {
        return buildResponse(
                "Badges retrieved successfully",
                HttpStatus.OK,
                badgeService.getBadgesByProductId(productId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getBadgeById(@PathVariable UUID id) {
        return buildResponse(
                "Badge retrieved successfully",
                HttpStatus.OK,
                badgeService.getBadgeById(id)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createBadge(@Valid @RequestBody CreateProductBadgeRequest request) {
        return buildResponse(
                "Badge created successfully",
                HttpStatus.CREATED,
                badgeService.createBadge(request)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<GeneralResponse> updateBadge(
            @Valid @RequestBody UpdateProductBadgeRequest request,
            @PathVariable UUID id
    ) {
        return buildResponse(
                "Badge updated successfully",
                HttpStatus.OK,
                badgeService.updateBadge(request, id)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteBadge(@PathVariable UUID id) {
        return buildResponse(
                "Badge deleted successfully",
                HttpStatus.OK,
                badgeService.deleteBadge(id)
        );
    }
}