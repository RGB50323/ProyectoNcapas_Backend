package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.productVariant.CreateProductVariantRequest;
import com.uca.ecommerce.domain.dto.request.productVariant.PatchProductVariantRequest;
import com.uca.ecommerce.domain.dto.request.productVariant.UpdateProductVariantRequest;
import com.uca.ecommerce.domain.dto.response.ProductVariantResponse;

import java.util.List;
import java.util.UUID;

public interface ProductVariantService {

    List<ProductVariantResponse> getAllProductVariants();

    List<ProductVariantResponse> getProductVariantsByProductId(UUID productId);

    ProductVariantResponse getProductVariantById(UUID id);

    ProductVariantResponse createProductVariant(CreateProductVariantRequest request);

    ProductVariantResponse updateProductVariant(UpdateProductVariantRequest request, UUID id);

    ProductVariantResponse patchProductVariant(PatchProductVariantRequest request, UUID id);

    ProductVariantResponse deleteProductVariant(UUID id);
}