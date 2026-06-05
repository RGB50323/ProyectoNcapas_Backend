package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.request.productImage.CreateProductImageRequest;
import com.uca.ecommerce.domain.dto.request.productImage.PatchProductImageRequest;
import com.uca.ecommerce.domain.dto.request.productImage.UpdateProductImageRequest;
import com.uca.ecommerce.domain.dto.response.ProductImageResponse;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.ProductImage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductImageMapper {

    public ProductImage toEntityCreate(
            CreateProductImageRequest request,
            Product product
    ) {
        return ProductImage.builder()
                .product(product)
                .url(request.getUrl())
                .altText(request.getAltText())
                .primaryImage(Boolean.TRUE.equals(request.getPrimaryImage()))
                .sortOrder(request.getSortOrder())
                .build();
    }

    public ProductImage toEntityUpdate(
            UpdateProductImageRequest request,
            ProductImage existing,
            Product product
    ) {
        return ProductImage.builder()
                .id(existing.getId())
                .product(product)
                .url(request.getUrl())
                .altText(request.getAltText())
                .primaryImage(request.getPrimaryImage())
                .sortOrder(request.getSortOrder())
                .build();
    }

    public ProductImage toEntityPatch(
            PatchProductImageRequest request,
            ProductImage existing,
            Product product
    ) {
        return ProductImage.builder()
                .id(existing.getId())
                .product(product != null ? product : existing.getProduct())
                .url(request.getUrl() != null ? request.getUrl() : existing.getUrl())
                .altText(Boolean.TRUE.equals(request.getRemoveAltText())
                        ? null
                        : request.getAltText() != null
                          ? request.getAltText()
                          : existing.getAltText())
                .primaryImage(request.getPrimaryImage() != null
                        ? request.getPrimaryImage()
                        : existing.isPrimaryImage())
                .sortOrder(request.getSortOrder() != null
                        ? request.getSortOrder()
                        : existing.getSortOrder())
                .build();
    }

    public ProductImageResponse toDto(ProductImage productImage) {
        return ProductImageResponse.builder()
                .id(productImage.getId())
                .productId(productImage.getProduct().getId())
                .productName(productImage.getProduct().getName())
                .productSku(productImage.getProduct().getSku())
                .productSlug(productImage.getProduct().getSlug())
                .url(productImage.getUrl())
                .altText(productImage.getAltText())
                .primaryImage(productImage.isPrimaryImage())
                .sortOrder(productImage.getSortOrder())
                .build();
    }

    public List<ProductImageResponse> toDtoList(List<ProductImage> productImages) {
        return productImages.stream()
                .map(this::toDto)
                .toList();
    }
}