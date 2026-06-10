package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.request.dropProduct.CreateDropProductRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.DropProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/drop-products")
@RequiredArgsConstructor
public class DropProductController extends BaseController {

    private final DropProductService dropProductService;

    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllDropProducts() {
        return buildResponse(
                "Drop products retrieved successfully",
                HttpStatus.OK,
                dropProductService.getAllDropProducts()
        );
    }

    @GetMapping("/drop/{dropId}")
    public ResponseEntity<GeneralResponse> getDropProductsByDropId(@PathVariable UUID dropId) {
        return buildResponse(
                "Drop products by drop retrieved successfully",
                HttpStatus.OK,
                dropProductService.getDropProductsByDropId(dropId)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createDropProduct(@Valid @RequestBody CreateDropProductRequest request) {
        return buildResponse(
                "Product assigned to drop successfully",
                HttpStatus.CREATED,
                dropProductService.createDropProduct(request)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteDropProduct(@PathVariable UUID id) {
        return buildResponse(
                "Product removed from drop successfully",
                HttpStatus.OK,
                dropProductService.deleteDropProduct(id)
        );
    }
}
