package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.Enums.AuthStatus;
import com.uca.ecommerce.common.mappers.ProductBadgeMapper;
import com.uca.ecommerce.domain.dto.request.productBadge.CreateProductBadgeRequest;
import com.uca.ecommerce.domain.dto.request.productBadge.UpdateProductBadgeRequest;
import com.uca.ecommerce.domain.dto.response.ProductBadgeResponse;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.ProductBadge;
import com.uca.ecommerce.exceptions.FieldAlreadyExistsException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.ProductBadgeRepository;
import com.uca.ecommerce.repository.ProductRepository;
import com.uca.ecommerce.services.ProductBadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductBadgeServiceImpl implements ProductBadgeService {

    private final ProductBadgeRepository badgeRepository;
    private final ProductRepository productRepository;
    private final ProductBadgeMapper badgeMapper;

    @Override
    public List<ProductBadgeResponse> getAllBadges() {
        return badgeMapper.toDtoList(badgeRepository.findAll());
    }

    @Override
    public List<ProductBadgeResponse> getPublicBadges() {
        return badgeMapper.toDtoList(
                badgeRepository.findByProduct_AuthStatusAndProduct_TotalStockGreaterThan(
                        AuthStatus.AUTHENTICATED,
                        0
                )
        );
    }

    @Override
    public List<ProductBadgeResponse> getBadgesByProductId(UUID productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        return badgeMapper.toDtoList(badgeRepository.findByProductId(productId));
    }

    @Override
    public ProductBadgeResponse getBadgeById(UUID id) {
        return badgeMapper.toDto(findOrThrow(id));
    }

    @Override
    public ProductBadgeResponse createBadge(CreateProductBadgeRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (badgeRepository.existsByProductIdAndLabelIgnoreCase(request.getProductId(), request.getLabel()))
            throw new FieldAlreadyExistsException(
                    "Badge with label '" + request.getLabel() + "' already exists for this product"
            );

        return badgeMapper.toDto(
                badgeRepository.save(badgeMapper.toEntityCreate(request, product))
        );
    }

    @Override
    public ProductBadgeResponse updateBadge(UpdateProductBadgeRequest request, UUID id) {
        ProductBadge existing = findOrThrow(id);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        boolean labelChanged = !existing.getLabel().equalsIgnoreCase(request.getLabel());
        boolean productChanged = !existing.getProduct().getId().equals(request.getProductId());

        if ((labelChanged || productChanged)
                && badgeRepository.existsByProductIdAndLabelIgnoreCase(request.getProductId(), request.getLabel()))
            throw new FieldAlreadyExistsException(
                    "Badge with label '" + request.getLabel() + "' already exists for this product"
            );

        return badgeMapper.toDto(
                badgeRepository.save(badgeMapper.toEntityUpdate(request, existing, product))
        );
    }

    @Override
    public ProductBadgeResponse deleteBadge(UUID id) {
        ProductBadge existing = findOrThrow(id);
        badgeRepository.deleteById(id);
        return badgeMapper.toDto(existing);
    }

    private ProductBadge findOrThrow(UUID id) {
        return badgeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product badge not found"));
    }
}