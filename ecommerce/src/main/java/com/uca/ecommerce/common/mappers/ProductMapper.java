package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.common.Enums.AuthStatus;
import com.uca.ecommerce.domain.dto.request.product.CreateProductRequest;
import com.uca.ecommerce.domain.dto.request.product.PatchProductRequest;
import com.uca.ecommerce.domain.dto.request.product.UpdateProductRequest;
import com.uca.ecommerce.domain.dto.response.ProductResponse;
import com.uca.ecommerce.domain.entities.Brand;
import com.uca.ecommerce.domain.entities.Category;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.SellerProfile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    public Product toEntityCreate(
            CreateProductRequest request,
            SellerProfile seller,
            Category category,
            Brand brand
    ) {
        return Product.builder()
                .seller(seller)
                .category(category)
                .brand(brand)
                .sku(request.getSku())
                .name(request.getName())
                .slug(request.getSlug())
                .description(request.getDescription())
                .price(request.getPrice())
                .condition(request.getCondition())
                .conditionScore(request.getConditionScore())
                .authStatus(request.getAuthStatus() != null
                        ? request.getAuthStatus()
                        : AuthStatus.PENDING)
                .featured(Boolean.TRUE.equals(request.getFeatured()))
                .newProduct(Boolean.TRUE.equals(request.getNewProduct()))
                .limited(Boolean.TRUE.equals(request.getLimited()))
                .privateDrop(Boolean.TRUE.equals(request.getPrivateDrop()))
                .totalStock(request.getTotalStock())
                .reviewCount(0)
                .build();
    }

    public Product toEntityUpdate(
            UpdateProductRequest request,
            Product existing,
            SellerProfile seller,
            Category category,
            Brand brand
    ) {
        return Product.builder()
                .id(existing.getId())
                .seller(seller)
                .category(category)
                .brand(brand)
                .sku(request.getSku())
                .name(request.getName())
                .slug(request.getSlug())
                .description(request.getDescription())
                .price(request.getPrice())
                .condition(request.getCondition())
                .conditionScore(request.getConditionScore())
                .authStatus(request.getAuthStatus())
                .featured(request.getFeatured())
                .newProduct(request.getNewProduct())
                .limited(request.getLimited())
                .privateDrop(request.getPrivateDrop())
                .totalStock(request.getTotalStock())
                .rating(existing.getRating())
                .reviewCount(existing.getReviewCount())
                .createdAt(existing.getCreatedAt())
                .build();
    }

    public Product toEntityPatch(
            PatchProductRequest request,
            Product existing,
            SellerProfile seller,
            Category category,
            Brand brand
    ) {
        return Product.builder()
                .id(existing.getId())
                .seller(seller != null ? seller : existing.getSeller())
                .category(category != null ? category : existing.getCategory())
                .brand(brand != null ? brand : existing.getBrand())
                .sku(request.getSku() != null ? request.getSku() : existing.getSku())
                .name(request.getName() != null ? request.getName() : existing.getName())
                .slug(request.getSlug() != null ? request.getSlug() : existing.getSlug())
                .description(Boolean.TRUE.equals(request.getRemoveDescription())
                        ? null
                        : request.getDescription() != null
                          ? request.getDescription()
                          : existing.getDescription())
                .price(request.getPrice() != null ? request.getPrice() : existing.getPrice())
                .condition(request.getCondition() != null ? request.getCondition() : existing.getCondition())
                .conditionScore(Boolean.TRUE.equals(request.getRemoveConditionScore())
                        ? null
                        : request.getConditionScore() != null
                          ? request.getConditionScore()
                          : existing.getConditionScore())
                .authStatus(request.getAuthStatus() != null ? request.getAuthStatus() : existing.getAuthStatus())
                .featured(request.getFeatured() != null ? request.getFeatured() : existing.isFeatured())
                .newProduct(request.getNewProduct() != null ? request.getNewProduct() : existing.isNewProduct())
                .limited(request.getLimited() != null ? request.getLimited() : existing.isLimited())
                .privateDrop(request.getPrivateDrop() != null ? request.getPrivateDrop() : existing.isPrivateDrop())
                .totalStock(request.getTotalStock() != null ? request.getTotalStock() : existing.getTotalStock())
                .rating(existing.getRating())
                .reviewCount(existing.getReviewCount())
                .createdAt(existing.getCreatedAt())
                .build();
    }

    public ProductResponse toDto(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .sellerId(product.getSeller().getId())
                .sellerStoreName(product.getSeller().getStoreName())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .brandId(product.getBrand().getId())
                .brandName(product.getBrand().getName())
                .sku(product.getSku())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .price(product.getPrice())
                .condition(product.getCondition())
                .conditionScore(product.getConditionScore())
                .authStatus(product.getAuthStatus())
                .featured(product.isFeatured())
                .newProduct(product.isNewProduct())
                .limited(product.isLimited())
                .privateDrop(product.isPrivateDrop())
                .totalStock(product.getTotalStock())
                .rating(product.getRating())
                .reviewCount(product.getReviewCount())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public List<ProductResponse> toDtoList(List<Product> products) {
        return products.stream()
                .map(this::toDto)
                .toList();
    }
}