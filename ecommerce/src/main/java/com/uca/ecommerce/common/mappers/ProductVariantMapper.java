package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.request.productVariant.CreateProductVariantRequest;
import com.uca.ecommerce.domain.dto.request.productVariant.PatchProductVariantRequest;
import com.uca.ecommerce.domain.dto.request.productVariant.UpdateProductVariantRequest;
import com.uca.ecommerce.domain.dto.response.ProductVariantResponse;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.ProductVariant;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductVariantMapper {

    public ProductVariant toEntityCreate(
            CreateProductVariantRequest request,
            Product product
    ) {
        return ProductVariant.builder()
                .product(product)
                .size(request.getSize())
                .colorName(request.getColorName())
                .colorHex(request.getColorHex())
                .stock(request.getStock())
                .priceDelta(request.getPriceDelta())
                .build();
    }

    public ProductVariant toEntityUpdate(
            UpdateProductVariantRequest request,
            ProductVariant existing,
            Product product
    ) {
        return ProductVariant.builder()
                .id(existing.getId())
                .product(product)
                .size(request.getSize())
                .colorName(request.getColorName())
                .colorHex(request.getColorHex())
                .stock(request.getStock())
                .priceDelta(request.getPriceDelta())
                .build();
    }

    public ProductVariant toEntityPatch(
            PatchProductVariantRequest request,
            ProductVariant existing,
            Product product
    ) {
        return ProductVariant.builder()
                .id(existing.getId())
                .product(product != null ? product : existing.getProduct())
                .size(request.getSize() != null ? request.getSize() : existing.getSize())
                .colorName(request.getColorName() != null ? request.getColorName() : existing.getColorName())
                .colorHex(request.getColorHex() != null ? request.getColorHex() : existing.getColorHex())
                .stock(request.getStock() != null ? request.getStock() : existing.getStock())
                .priceDelta(request.getPriceDelta() != null ? request.getPriceDelta() : existing.getPriceDelta())
                .build();
    }

    public ProductVariantResponse toDto(ProductVariant productVariant) {
        return ProductVariantResponse.builder()
                .id(productVariant.getId())
                .productId(productVariant.getProduct().getId())
                .productName(productVariant.getProduct().getName())
                .productSku(productVariant.getProduct().getSku())
                .productSlug(productVariant.getProduct().getSlug())
                .size(productVariant.getSize())
                .colorName(productVariant.getColorName())
                .colorHex(productVariant.getColorHex())
                .stock(productVariant.getStock())
                .priceDelta(productVariant.getPriceDelta())
                .build();
    }

    public List<ProductVariantResponse> toDtoList(List<ProductVariant> productVariants) {
        return productVariants.stream()
                .map(this::toDto)
                .toList();
    }
}