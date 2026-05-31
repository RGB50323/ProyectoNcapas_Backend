package com.uca.ecommerce.controller;


import com.uca.ecommerce.domain.dto.request.brand.CreateBrandRequest;
import com.uca.ecommerce.domain.dto.request.brand.PatchBrandRequest;
import com.uca.ecommerce.domain.dto.request.brand.UpdateBrandRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
public class BrandController extends BaseController{

    private final BrandService brandService;

    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllBrands() {
        return buildResponse(
                "Brands retrieved successfully",
                HttpStatus.OK,
                brandService.getAllBrands()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getBrandById(@PathVariable UUID id) {
        return buildResponse(
                "Brand retrieved successfully",
                HttpStatus.OK,
                brandService.getBrandById(id)
        );
    }

    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createBrand(@Valid @RequestBody CreateBrandRequest request) {
        return buildResponse(
                "Brand created successfully",
                HttpStatus.CREATED,
                brandService.createBrand(request)
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GeneralResponse> updateBrand(@Valid @RequestBody UpdateBrandRequest request, @PathVariable UUID id) {
        return buildResponse(
                "Brand updated successfully",
                HttpStatus.OK,
                brandService.updateBrand(request, id)
        );
    }

    @PatchMapping("/patch/{id}")
    public ResponseEntity<GeneralResponse> patchBrand(@Valid @RequestBody PatchBrandRequest request, @PathVariable UUID id) {
        return buildResponse(
                "Brand partially updated successfully",
                HttpStatus.OK,
                brandService.patchBrand(request, id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteBrand(@PathVariable UUID id) {
        return buildResponse(
                "Brand deleted successfully",
                HttpStatus.OK,
                brandService.deleteBrand(id)
        );
    }

}
