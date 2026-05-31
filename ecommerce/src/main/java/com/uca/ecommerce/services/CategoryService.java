package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.category.CreateCategoryRequest;
import com.uca.ecommerce.domain.dto.request.category.PatchCategoryRequest;
import com.uca.ecommerce.domain.dto.request.category.UpdateCategoryRequest;
import com.uca.ecommerce.domain.dto.response.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(UUID id);
    CategoryResponse createCategory(CreateCategoryRequest request);
    CategoryResponse updateCategory(UpdateCategoryRequest request, UUID id);
    CategoryResponse deleteCategory(UUID id);
    CategoryResponse patchCategory(PatchCategoryRequest request, UUID id);
}
