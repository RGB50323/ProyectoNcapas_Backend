package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.response.ProductResponse;

import java.util.List;

public interface ProductRecommendationService {

    List<ProductResponse> getRecommendationsForCurrentRequest(Integer limit);
}
