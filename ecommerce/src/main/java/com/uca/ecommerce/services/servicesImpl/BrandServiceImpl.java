package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.mappers.BrandMapper;
import com.uca.ecommerce.domain.dto.request.brand.CreateBrandRequest;
import com.uca.ecommerce.domain.dto.request.brand.PatchBrandRequest;
import com.uca.ecommerce.domain.dto.request.brand.UpdateBrandRequest;
import com.uca.ecommerce.domain.dto.response.BrandResponse;
import com.uca.ecommerce.domain.entities.Brand;
import com.uca.ecommerce.exceptions.FieldAlreadyExistsException;
import com.uca.ecommerce.exceptions.InvalidBrandPatchException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.BrandRepository;
import com.uca.ecommerce.services.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    public List<BrandResponse> getAllBrands() {
        return brandMapper.toDtoList(brandRepository.findAll());
    }

    @Override
    public BrandResponse getBrandById(UUID id) {
        return brandMapper.toDto(
                brandRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Brand not found"))
        );
    }

    @Override
    public BrandResponse createBrand(CreateBrandRequest request) {
        if (brandRepository.existsByName(request.getName())) {
            throw new FieldAlreadyExistsException("Brand name already exists");
        }

        if (brandRepository.existsBySlug(request.getSlug())) {
            throw new FieldAlreadyExistsException("Brand slug already exists");
        }

        return brandMapper.toDto(
                brandRepository.save(
                        brandMapper.toEntityCreate(request)
                )
        );
    }

    @Override
    public BrandResponse updateBrand(UpdateBrandRequest request, UUID id) {
        Brand existing = brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Brand not found"));

        if (!existing.getName().equals(request.getName())
                && brandRepository.existsByName(request.getName())) {
            throw new FieldAlreadyExistsException("Brand name already exists");
        }

        if (!existing.getSlug().equals(request.getSlug())
                && brandRepository.existsBySlug(request.getSlug())) {
            throw new FieldAlreadyExistsException("Brand slug already exists");
        }

        return brandMapper.toDto(
                brandRepository.save(
                        brandMapper.toEntityUpdate(request, existing)
                )
        );
    }

    @Override
    public BrandResponse patchBrand(PatchBrandRequest request, UUID id) {
        Brand existing = brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Brand not found"));

        if (Boolean.TRUE.equals(request.getRemoveLogo())
                && request.getLogoUrl() != null) {
            throw new InvalidBrandPatchException(
                    "Logo URL cannot be provided when removeLogo is true"
            );
        }

        if (request.getName() != null
                && !existing.getName().equals(request.getName())
                && brandRepository.existsByName(request.getName())) {
            throw new FieldAlreadyExistsException("Brand name already exists");
        }

        if (request.getSlug() != null
                && !existing.getSlug().equals(request.getSlug())
                && brandRepository.existsBySlug(request.getSlug())) {
            throw new FieldAlreadyExistsException("Brand slug already exists");
        }

        return brandMapper.toDto(
                brandRepository.save(
                        brandMapper.toEntityPatch(request, existing)
                )
        );
    }

    @Override
    public BrandResponse deleteBrand(UUID id) {
        Brand existing = brandRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Brand not found"));

        brandRepository.deleteById(id);
        return brandMapper.toDto(existing);
    }
}


