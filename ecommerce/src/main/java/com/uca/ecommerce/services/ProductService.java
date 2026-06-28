package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.product.CreateProductRequest;
import com.uca.ecommerce.domain.dto.request.product.PatchProductRequest;
import com.uca.ecommerce.domain.dto.request.product.UpdateProductRequest;
import com.uca.ecommerce.domain.dto.response.ProductResponse;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    List<ProductResponse> getAllProducts();

    List<ProductResponse> getPublicProducts();

    ProductResponse getProductById(UUID id);

    ProductResponse createProduct(CreateProductRequest request);

    ProductResponse updateProduct(UpdateProductRequest request, UUID id);

    ProductResponse patchProduct(PatchProductRequest request, UUID id);

    ProductResponse deleteProduct(UUID id);

    List<ProductResponse> getProductsBySellerEmail(String email);
}