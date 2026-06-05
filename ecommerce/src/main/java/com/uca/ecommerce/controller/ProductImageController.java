package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.request.productImage.CreateProductImageRequest;
import com.uca.ecommerce.domain.dto.request.productImage.PatchProductImageRequest;
import com.uca.ecommerce.domain.dto.request.productImage.UpdateProductImageRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.ProductImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/product-images")
@RequiredArgsConstructor
public class ProductImageController extends BaseController {

    private final ProductImageService productImageService;

    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllProductImages() {
        return buildResponse(
                "Product images retrieved successfully",
                HttpStatus.OK,
                productImageService.getAllProductImages()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getProductImageById(@PathVariable UUID id) {
        return buildResponse(
                "Product image retrieved successfully",
                HttpStatus.OK,
                productImageService.getProductImageById(id)
        );
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<GeneralResponse> getProductImagesByProductId(@PathVariable UUID productId) {
        return buildResponse(
                "Product images by product retrieved successfully",
                HttpStatus.OK,
                productImageService.getProductImagesByProductId(productId)
        );
    }

    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createProductImage(
            @Valid @RequestBody CreateProductImageRequest request
    ) {
        return buildResponse(
                "Product image created successfully",
                HttpStatus.CREATED,
                productImageService.createProductImage(request)
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GeneralResponse> updateProductImage(
            @Valid @RequestBody UpdateProductImageRequest request,
            @PathVariable UUID id
    ) {
        return buildResponse(
                "Product image updated successfully",
                HttpStatus.OK,
                productImageService.updateProductImage(request, id)
        );
    }

    @PatchMapping("/patch/{id}")
    public ResponseEntity<GeneralResponse> patchProductImage(
            @Valid @RequestBody PatchProductImageRequest request,
            @PathVariable UUID id
    ) {
        return buildResponse(
                "Product image partially updated successfully",
                HttpStatus.OK,
                productImageService.patchProductImage(request, id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteProductImage(@PathVariable UUID id) {
        return buildResponse(
                "Product image deleted successfully",
                HttpStatus.OK,
                productImageService.deleteProductImage(id)
        );
    }
}