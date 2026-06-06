package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.request.productBadge.CreateProductBadgeRequest;
import com.uca.ecommerce.domain.dto.request.productBadge.UpdateProductBadgeRequest;
import com.uca.ecommerce.domain.dto.response.ProductBadgeResponse;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.ProductBadge;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductBadgeMapper {

    public ProductBadge toEntityCreate(CreateProductBadgeRequest request, Product product) {
        return ProductBadge.builder()
                .product(product)
                .label(request.getLabel())
                .build();
    }

    public ProductBadge toEntityUpdate(UpdateProductBadgeRequest request, ProductBadge existing, Product product) {
        return ProductBadge.builder()
                .id(existing.getId())
                .product(product)
                .label(request.getLabel())
                .build();
    }

    public ProductBadgeResponse toDto(ProductBadge badge) {
        return ProductBadgeResponse.builder()
                .id(badge.getId())
                .productId(badge.getProduct().getId())
                .productName(badge.getProduct().getName())
                .label(badge.getLabel())
                .build();
    }

    public List<ProductBadgeResponse> toDtoList(List<ProductBadge> badges) {
        return badges.stream()
                .map(this::toDto)
                .toList();
    }
}