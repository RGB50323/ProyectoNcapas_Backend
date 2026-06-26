package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.productBadge.CreateProductBadgeRequest;
import com.uca.ecommerce.domain.dto.request.productBadge.UpdateProductBadgeRequest;
import com.uca.ecommerce.domain.dto.response.ProductBadgeResponse;

import java.util.List;
import java.util.UUID;

public interface ProductBadgeService {
    List<ProductBadgeResponse> getAllBadges();
    List<ProductBadgeResponse> getPublicBadges();
    List<ProductBadgeResponse> getBadgesByProductId(UUID productId);
    ProductBadgeResponse getBadgeById(UUID id);
    ProductBadgeResponse createBadge(CreateProductBadgeRequest request);
    ProductBadgeResponse updateBadge(UpdateProductBadgeRequest request, UUID id);
    ProductBadgeResponse deleteBadge(UUID id);
}