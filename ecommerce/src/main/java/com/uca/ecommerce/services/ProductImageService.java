package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.productImage.CreateProductImageRequest;
import com.uca.ecommerce.domain.dto.request.productImage.PatchProductImageRequest;
import com.uca.ecommerce.domain.dto.request.productImage.UpdateProductImageRequest;
import com.uca.ecommerce.domain.dto.response.ProductImageResponse;

import java.util.List;
import java.util.UUID;

public interface ProductImageService {

    List<ProductImageResponse> getAllProductImages();

    List<ProductImageResponse> getPublicProductImages();

    List<ProductImageResponse> getProductImagesByProductId(UUID productId);

    ProductImageResponse getProductImageById(UUID id);

    ProductImageResponse createProductImage(CreateProductImageRequest request);

    ProductImageResponse updateProductImage(UpdateProductImageRequest request, UUID id);

    ProductImageResponse patchProductImage(PatchProductImageRequest request, UUID id);

    ProductImageResponse deleteProductImage(UUID id);
}