package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.mappers.CategoryMapper;
import com.uca.ecommerce.domain.dto.request.category.CreateCategoryRequest;
import com.uca.ecommerce.domain.dto.request.category.PatchCategoryRequest;
import com.uca.ecommerce.domain.dto.request.category.UpdateCategoryRequest;
import com.uca.ecommerce.domain.dto.response.CategoryResponse;
import com.uca.ecommerce.domain.entities.Category;
import com.uca.ecommerce.exceptions.CategoryHasChildrenException;
import com.uca.ecommerce.exceptions.InvalidCategoryHierarchyException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.CategoryRepository;
import com.uca.ecommerce.repository.ProductRepository;
import com.uca.ecommerce.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductRepository productRepository;

    private CategoryResponse withProductCount(Category category) {
        CategoryResponse dto = categoryMapper.toDto(category);
        dto.setUnits((int) productRepository.countByCategory_Id(category.getId()));
        return dto;
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream().map(this::withProductCount).toList();
    }

    @Override
    public CategoryResponse getCategoryById(UUID id) {
        return withProductCount(
                categoryRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Category not found"))
        );
    }

    @Override
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Category parent = null;

        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new NotFoundException("Parent category not found"));
        }

        return categoryMapper.toDto(
                categoryRepository.save(
                        categoryMapper.toEntityCreate(request, parent)
                )
        );
    }

    @Override
    public CategoryResponse updateCategory(UpdateCategoryRequest request, UUID id) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Category parent = null;

        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new NotFoundException("Parent category not found"));
        }

        validateHierarchy(id, parent);

        return categoryMapper.toDto(
                categoryRepository.save(
                        categoryMapper.toEntityUpdate(request, existing, parent)
                )
        );
    }

    @Override
    public CategoryResponse patchCategory(PatchCategoryRequest request, UUID id) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Category parent = null;

        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new NotFoundException("Parent category not found"));
        }

        if (Boolean.TRUE.equals(request.getRemoveParent())
                && request.getParentId() != null) {
            throw new InvalidCategoryHierarchyException(
                    "Parent ID cannot be provided when removeParent is true"
            );
        }

        validateHierarchy(id, parent);

        return categoryMapper.toDto(
                categoryRepository.save(
                        categoryMapper.toEntityPatch(request, existing, parent)
                )
        );
    }



    @Override
    public CategoryResponse deleteCategory(UUID id) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));

        if (categoryRepository.existsByParentId(id)) {
            throw new CategoryHasChildrenException(
                    "Category cannot be deleted because it has subcategories"
            );
        }

        categoryRepository.deleteById(id);
        return categoryMapper.toDto(existing);
    }

    private void validateHierarchy(UUID categoryId, Category parent) {
        Category current = parent;

        while (current != null) {
            if (categoryId.equals(current.getId())) {
                throw new InvalidCategoryHierarchyException(
                        "A category cannot be its own parent or ancestor"
                );
            }

            current = current.getParent();
        }
    }






}
