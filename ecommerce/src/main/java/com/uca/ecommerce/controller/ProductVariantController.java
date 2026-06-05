package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.request.productVariant.CreateProductVariantRequest;
import com.uca.ecommerce.domain.dto.request.productVariant.PatchProductVariantRequest;
import com.uca.ecommerce.domain.dto.request.productVariant.UpdateProductVariantRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.ProductVariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/product-variants")
@RequiredArgsConstructor
public class ProductVariantController extends BaseController {

    private final ProductVariantService productVariantService;

    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllProductVariants() {
        return buildResponse(
                "Product variants retrieved successfully",
                HttpStatus.OK,
                productVariantService.getAllProductVariants()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getProductVariantById(@PathVariable UUID id) {
        return buildResponse(
                "Product variant retrieved successfully",
                HttpStatus.OK,
                productVariantService.getProductVariantById(id)
        );
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<GeneralResponse> getProductVariantsByProductId(@PathVariable UUID productId) {
        return buildResponse(
                "Product variants by product retrieved successfully",
                HttpStatus.OK,
                productVariantService.getProductVariantsByProductId(productId)
        );
    }

    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createProductVariant(
            @Valid @RequestBody CreateProductVariantRequest request
    ) {
        return buildResponse(
                "Product variant created successfully",
                HttpStatus.CREATED,
                productVariantService.createProductVariant(request)
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GeneralResponse> updateProductVariant(
            @Valid @RequestBody UpdateProductVariantRequest request,
            @PathVariable UUID id
    ) {
        return buildResponse(
                "Product variant updated successfully",
                HttpStatus.OK,
                productVariantService.updateProductVariant(request, id)
        );
    }

    @PatchMapping("/patch/{id}")
    public ResponseEntity<GeneralResponse> patchProductVariant(
            @Valid @RequestBody PatchProductVariantRequest request,
            @PathVariable UUID id
    ) {
        return buildResponse(
                "Product variant partially updated successfully",
                HttpStatus.OK,
                productVariantService.patchProductVariant(request, id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteProductVariant(@PathVariable UUID id) {
        return buildResponse(
                "Product variant deleted successfully",
                HttpStatus.OK,
                productVariantService.deleteProductVariant(id)
        );
    }
}