package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.request.brand.CreateBrandRequest;
import com.uca.ecommerce.domain.dto.request.brand.PatchBrandRequest;
import com.uca.ecommerce.domain.dto.request.brand.UpdateBrandRequest;
import com.uca.ecommerce.domain.dto.response.BrandResponse;
import com.uca.ecommerce.domain.entities.Brand;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BrandMapper {

    public Brand toEntityCreate(CreateBrandRequest request) {
        return Brand.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .logoUrl(request.getLogoUrl())
                .build();
    }

    public Brand toEntityUpdate(UpdateBrandRequest request, Brand existing) {
        return Brand.builder()
                .id(existing.getId())
                .name(request.getName())
                .slug(request.getSlug())
                .logoUrl(request.getLogoUrl())
                .build();
    }

    public Brand toEntityPatch(PatchBrandRequest request, Brand existing) {
        return Brand.builder()
                .id(existing.getId())
                .name(request.getName() != null
                        ? request.getName()
                        : existing.getName())
                .slug(request.getSlug() != null
                        ? request.getSlug()
                        : existing.getSlug())
                .logoUrl(Boolean.TRUE.equals(request.getRemoveLogo())
                        ? null
                        : request.getLogoUrl() != null
                          ? request.getLogoUrl()
                          : existing.getLogoUrl())
                .build();
    }

    public BrandResponse toDto(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .slug(brand.getSlug())
                .logoUrl(brand.getLogoUrl())
                .build();
    }

    public List<BrandResponse> toDtoList(List<Brand> brands) {
        return brands.stream()
                .map(this::toDto)
                .toList();
    }

}
