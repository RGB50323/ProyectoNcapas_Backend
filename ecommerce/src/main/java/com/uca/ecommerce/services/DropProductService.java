package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.dropProduct.CreateDropProductRequest;
import com.uca.ecommerce.domain.dto.response.DropProductResponse;

import java.util.List;
import java.util.UUID;

public interface DropProductService {

    List<DropProductResponse> getAllDropProducts();

    List<DropProductResponse> getDropProductsByDropId(UUID dropId);

    DropProductResponse createDropProduct(CreateDropProductRequest request);

    DropProductResponse deleteDropProduct(UUID id);
}
