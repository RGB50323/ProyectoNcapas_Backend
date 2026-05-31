package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.request.category.CreateCategoryRequest;
import com.uca.ecommerce.domain.dto.request.category.PatchCategoryRequest;
import com.uca.ecommerce.domain.dto.request.category.UpdateCategoryRequest;
import com.uca.ecommerce.domain.dto.response.CategoryResponse;
import com.uca.ecommerce.domain.entities.Category;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryMapper {

    public Category toEntityCreate(CreateCategoryRequest request, Category parent) {
        return Category.builder()
                .name(request.getName())
                .units(request.getUnits())
                .parent(parent)
                .build();
    }

    public Category toEntityUpdate(
            UpdateCategoryRequest request,
            Category existing,
            Category parent
    ) {
        return Category.builder()
                .id(existing.getId())
                .name(request.getName())
                .units(request.getUnits())
                .parent(parent)
                .build();
    }

    public CategoryResponse toDto(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .units(category.getUnits())
                .parentId(category.getParent() == null
                        ? null
                        : category.getParent().getId())
                .build();
    }

    public List<CategoryResponse> toDtoList(List<Category> categories) {
        return categories.stream()
                .map(this::toDto)
                .toList();
    }

    public Category toEntityPatch(PatchCategoryRequest request, Category existing, Category parent) {
        return Category.builder()
                .id(existing.getId())
                .name(request.getName() != null
                        ? request.getName()
                        : existing.getName())
                .units(request.getUnits() != null
                        ? request.getUnits()
                        : existing.getUnits())
                .parent(Boolean.TRUE.equals(request.getRemoveParent())
                        ? null
                        : parent != null
                          ? parent
                          : existing.getParent())
                .build();
    }

}
