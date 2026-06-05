package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.mappers.ProductVariantMapper;
import com.uca.ecommerce.domain.dto.request.productVariant.CreateProductVariantRequest;
import com.uca.ecommerce.domain.dto.request.productVariant.PatchProductVariantRequest;
import com.uca.ecommerce.domain.dto.request.productVariant.UpdateProductVariantRequest;
import com.uca.ecommerce.domain.dto.response.ProductVariantResponse;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.ProductVariant;
import com.uca.ecommerce.exceptions.FieldAlreadyExistsException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.ProductRepository;
import com.uca.ecommerce.repository.ProductVariantRepository;
import com.uca.ecommerce.services.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final ProductVariantMapper productVariantMapper;

    @Override
    public List<ProductVariantResponse> getAllProductVariants() {
        return productVariantMapper.toDtoList(productVariantRepository.findAll());
    }

    @Override
    public List<ProductVariantResponse> getProductVariantsByProductId(UUID productId) {
        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("Product not found");
        }

        return productVariantMapper.toDtoList(
                productVariantRepository.findByProductId(productId)
        );
    }

    @Override
    public ProductVariantResponse getProductVariantById(UUID id) {
        return productVariantMapper.toDto(
                productVariantRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Product variant not found"))
        );
    }

    @Override
    public ProductVariantResponse createProductVariant(CreateProductVariantRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (productVariantRepository.existsByProductIdAndSizeAndColorName(
                request.getProductId(),
                request.getSize(),
                request.getColorName()
        )) {
            throw new FieldAlreadyExistsException(
                    "Product variant already exists for this product, size and color"
            );
        }

        return productVariantMapper.toDto(
                productVariantRepository.save(
                        productVariantMapper.toEntityCreate(request, product)
                )
        );
    }

    @Override
    public ProductVariantResponse updateProductVariant(UpdateProductVariantRequest request, UUID id) {
        ProductVariant existing = productVariantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product variant not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        boolean changedUniqueFields =
                !existing.getProduct().getId().equals(request.getProductId())
                        || !existing.getSize().equals(request.getSize())
                        || !existing.getColorName().equals(request.getColorName());

        if (changedUniqueFields
                && productVariantRepository.existsByProductIdAndSizeAndColorName(
                request.getProductId(),
                request.getSize(),
                request.getColorName()
        )) {
            throw new FieldAlreadyExistsException(
                    "Product variant already exists for this product, size and color"
            );
        }

        return productVariantMapper.toDto(
                productVariantRepository.save(
                        productVariantMapper.toEntityUpdate(request, existing, product)
                )
        );
    }

    @Override
    public ProductVariantResponse patchProductVariant(PatchProductVariantRequest request, UUID id) {
        ProductVariant existing = productVariantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product variant not found"));

        Product product = null;

        if (request.getProductId() != null) {
            product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found"));
        }

        UUID finalProductId = request.getProductId() != null
                ? request.getProductId()
                : existing.getProduct().getId();

        String finalSize = request.getSize() != null
                ? request.getSize()
                : existing.getSize();

        String finalColorName = request.getColorName() != null
                ? request.getColorName()
                : existing.getColorName();

        boolean changedUniqueFields =
                !existing.getProduct().getId().equals(finalProductId)
                        || !existing.getSize().equals(finalSize)
                        || !existing.getColorName().equals(finalColorName);

        if (changedUniqueFields
                && productVariantRepository.existsByProductIdAndSizeAndColorName(
                finalProductId,
                finalSize,
                finalColorName
        )) {
            throw new FieldAlreadyExistsException(
                    "Product variant already exists for this product, size and color"
            );
        }

        return productVariantMapper.toDto(
                productVariantRepository.save(
                        productVariantMapper.toEntityPatch(request, existing, product)
                )
        );
    }

    @Override
    public ProductVariantResponse deleteProductVariant(UUID id) {
        ProductVariant existing = productVariantRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product variant not found"));

        productVariantRepository.deleteById(id);

        return productVariantMapper.toDto(existing);
    }
}