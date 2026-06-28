package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.brand.CreateBrandRequest;
import com.uca.ecommerce.domain.dto.request.brand.PatchBrandRequest;
import com.uca.ecommerce.domain.dto.request.brand.UpdateBrandRequest;
import com.uca.ecommerce.domain.dto.response.BrandResponse;

import java.util.List;
import java.util.UUID;

public interface BrandService {

    List<BrandResponse> getAllBrands();

    BrandResponse getBrandById(UUID id);

    BrandResponse createBrand(CreateBrandRequest request);

    BrandResponse updateBrand(UpdateBrandRequest request, UUID id);

    BrandResponse patchBrand(PatchBrandRequest request, UUID id);

    BrandResponse deleteBrand(UUID id);
}
