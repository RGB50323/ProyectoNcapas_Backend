package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.request.category.CreateCategoryRequest;
import com.uca.ecommerce.domain.dto.request.category.PatchCategoryRequest;
import com.uca.ecommerce.domain.dto.request.category.UpdateCategoryRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController extends BaseController {

    private final CategoryService categoryService;

    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllCategories() {
        return buildResponse(
                "Categories retrieved successfully",
                HttpStatus.OK, categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getCategoryById(@PathVariable UUID id) {
        return buildResponse(
                "Category retrieved successfully",
                HttpStatus.OK, categoryService.getCategoryById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        return buildResponse(
                "Category created successfully",
                HttpStatus.CREATED,
                categoryService.createCategory(request)
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GeneralResponse> updateCategory(@Valid @RequestBody UpdateCategoryRequest request, @PathVariable UUID id) {
        return buildResponse(
                "Category updated successfully",
                HttpStatus.OK,
                categoryService.updateCategory(request, id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteCategory(@PathVariable UUID id) {
        return buildResponse(
                "Category deleted successfully",
                HttpStatus.OK,
                categoryService.deleteCategory(id)
        );
    }

    @PatchMapping("/patch/{id}")
    public ResponseEntity<GeneralResponse> patchCategory(@Valid @RequestBody PatchCategoryRequest request, @PathVariable UUID id) {
        return buildResponse(
                "Category partially updated successfully",
                HttpStatus.OK,
                categoryService.patchCategory(request, id)
        );
    }

}
