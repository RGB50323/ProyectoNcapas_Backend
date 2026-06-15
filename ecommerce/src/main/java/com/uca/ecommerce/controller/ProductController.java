package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.request.product.CreateProductRequest;
import com.uca.ecommerce.domain.dto.request.product.PatchProductRequest;
import com.uca.ecommerce.domain.dto.request.product.UpdateProductRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController extends BaseController {

    private final ProductService productService;

    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllProducts() {
        return buildResponse(
                "Products retrieved successfully",
                HttpStatus.OK,
                productService.getAllProducts()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getProductById(@PathVariable UUID id) {
        return buildResponse(
                "Product retrieved successfully",
                HttpStatus.OK,
                productService.getProductById(id)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return buildResponse(
                "Product created successfully",
                HttpStatus.CREATED,
                productService.createProduct(request)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<GeneralResponse> updateProduct(
            @Valid @RequestBody UpdateProductRequest request,
            @PathVariable UUID id
    ) {
        return buildResponse(
                "Product updated successfully",
                HttpStatus.OK,
                productService.updateProduct(request, id)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @PatchMapping("/patch/{id}")
    public ResponseEntity<GeneralResponse> patchProduct(
            @Valid @RequestBody PatchProductRequest request,
            @PathVariable UUID id
    ) {
        return buildResponse(
                "Product partially updated successfully",
                HttpStatus.OK,
                productService.patchProduct(request, id)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteProduct(@PathVariable UUID id) {
        return buildResponse(
                "Product deleted successfully",
                HttpStatus.OK,
                productService.deleteProduct(id)
        );
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/my")
    public ResponseEntity<GeneralResponse> getMyProducts() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return buildResponse(
                "Seller products retrieved successfully",
                HttpStatus.OK,
                productService.getProductsBySellerEmail(email)
        );
    }
}