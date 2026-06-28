package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.Enums.AuthStatus;
import com.uca.ecommerce.common.mappers.ProductImageMapper;
import com.uca.ecommerce.domain.dto.request.productImage.CreateProductImageRequest;
import com.uca.ecommerce.domain.dto.request.productImage.PatchProductImageRequest;
import com.uca.ecommerce.domain.dto.request.productImage.UpdateProductImageRequest;
import com.uca.ecommerce.domain.dto.response.ProductImageResponse;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.ProductImage;
import com.uca.ecommerce.exceptions.FieldAlreadyExistsException;
import com.uca.ecommerce.exceptions.InvalidProductPatchException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.ProductImageRepository;
import com.uca.ecommerce.repository.ProductRepository;
import com.uca.ecommerce.security.SellerOwnershipService;
import com.uca.ecommerce.services.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ProductImageMapper productImageMapper;
    private final SellerOwnershipService sellerOwnershipService;

    @Override
    public List<ProductImageResponse> getAllProductImages() {
        return productImageMapper.toDtoList(productImageRepository.findAll());
    }

    @Override
    public List<ProductImageResponse> getPublicProductImages() {
        return productImageMapper.toDtoList(
                productImageRepository.findByProduct_AuthStatusAndProduct_TotalStockGreaterThan(
                        AuthStatus.AUTHENTICATED,
                        0
                )
        );
    }

    @Override
    public List<ProductImageResponse> getProductImagesByProductId(UUID productId) {
        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("Product not found");
        }

        return productImageMapper.toDtoList(
                productImageRepository.findByProductId(productId)
        );
    }

    @Override
    public ProductImageResponse getProductImageById(UUID id) {
        return productImageMapper.toDto(
                productImageRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Product image not found"))
        );
    }

    @Override
    public ProductImageResponse createProductImage(CreateProductImageRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));
        sellerOwnershipService.validateSellerOwnsProduct(product);

        if (productImageRepository.existsByProductIdAndUrl(
                request.getProductId(),
                request.getUrl()
        )) {
            throw new FieldAlreadyExistsException(
                    "Product image already exists for this product"
            );
        }

        if (Boolean.TRUE.equals(request.getPrimaryImage())) {
            removePreviousPrimaryImage(request.getProductId());
        }

        return productImageMapper.toDto(
                productImageRepository.save(
                        productImageMapper.toEntityCreate(request, product)
                )
        );
    }

    @Override
    public ProductImageResponse updateProductImage(UpdateProductImageRequest request, UUID id) {
        ProductImage existing = productImageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product image not found"));

        sellerOwnershipService.validateSellerOwnsProduct(existing.getProduct());

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));
        sellerOwnershipService.validateSellerOwnsProduct(product);

        boolean changedUniqueFields =
                !existing.getProduct().getId().equals(request.getProductId())
                        || !existing.getUrl().equals(request.getUrl());

        if (changedUniqueFields
                && productImageRepository.existsByProductIdAndUrl(
                request.getProductId(),
                request.getUrl()
        )) {
            throw new FieldAlreadyExistsException(
                    "Product image already exists for this product"
            );
        }

        if (Boolean.TRUE.equals(request.getPrimaryImage())) {
            removePreviousPrimaryImage(request.getProductId());
        }

        return productImageMapper.toDto(
                productImageRepository.save(
                        productImageMapper.toEntityUpdate(request, existing, product)
                )
        );
    }

    @Override
    public ProductImageResponse patchProductImage(PatchProductImageRequest request, UUID id) {
        ProductImage existing = productImageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product image not found"));

        sellerOwnershipService.validateSellerOwnsProduct(existing.getProduct());

        if (Boolean.TRUE.equals(request.getRemoveAltText())
                && request.getAltText() != null) {
            throw new InvalidProductPatchException(
                    "Alt text cannot be provided when removeAltText is true"
            );
        }

        Product product = null;

        if (request.getProductId() != null) {
            product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found"));
            sellerOwnershipService.validateSellerOwnsProduct(product);
        }

        UUID finalProductId = request.getProductId() != null
                ? request.getProductId()
                : existing.getProduct().getId();

        String finalUrl = request.getUrl() != null
                ? request.getUrl()
                : existing.getUrl();

        boolean changedUniqueFields =
                !existing.getProduct().getId().equals(finalProductId)
                        || !existing.getUrl().equals(finalUrl);

        if (changedUniqueFields
                && productImageRepository.existsByProductIdAndUrl(finalProductId, finalUrl)) {
            throw new FieldAlreadyExistsException(
                    "Product image already exists for this product"
            );
        }

        if (Boolean.TRUE.equals(request.getPrimaryImage())) {
            removePreviousPrimaryImage(finalProductId);
        }

        return productImageMapper.toDto(
                productImageRepository.save(
                        productImageMapper.toEntityPatch(request, existing, product)
                )
        );
    }

    @Override
    public ProductImageResponse deleteProductImage(UUID id) {
        ProductImage existing = productImageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product image not found"));

        sellerOwnershipService.validateSellerOwnsProduct(existing.getProduct());

        productImageRepository.deleteById(id);

        return productImageMapper.toDto(existing);
    }

    private void removePreviousPrimaryImage(UUID productId) {
        List<ProductImage> primaryImages = productImageRepository.findByProductIdAndPrimaryImageTrue(productId);

        primaryImages.forEach(image -> {
            image.setPrimaryImage(false);
            productImageRepository.save(image);
        });
    }
}