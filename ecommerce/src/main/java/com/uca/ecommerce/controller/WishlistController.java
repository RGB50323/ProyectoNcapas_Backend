package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.request.wishlist.CreateWishlistRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController extends BaseController {

    private final WishlistService wishlistService;

    @PreAuthorize("hasRole('BUYER')")
    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getMyWishlist() {
        return buildResponse(
                "Wishlist retrieved successfully",
                HttpStatus.OK,
                wishlistService.getMyWishlist()
        );
    }

    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> addToWishlist(@Valid @RequestBody CreateWishlistRequest request) {
        return buildResponse(
                "Product added to wishlist successfully",
                HttpStatus.CREATED,
                wishlistService.addToWishlist(request)
        );
    }

    @PreAuthorize("hasRole('BUYER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> removeFromWishlist(@PathVariable UUID id) {
        return buildResponse(
                "Product removed from wishlist successfully",
                HttpStatus.OK,
                wishlistService.removeFromWishlist(id)
        );
    }
}
