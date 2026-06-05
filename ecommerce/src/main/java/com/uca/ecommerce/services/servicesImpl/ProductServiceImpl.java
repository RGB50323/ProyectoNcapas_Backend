package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.mappers.ProductMapper;
import com.uca.ecommerce.domain.dto.request.product.CreateProductRequest;
import com.uca.ecommerce.domain.dto.request.product.PatchProductRequest;
import com.uca.ecommerce.domain.dto.request.product.UpdateProductRequest;
import com.uca.ecommerce.domain.dto.response.ProductResponse;
import com.uca.ecommerce.domain.entities.Brand;
import com.uca.ecommerce.domain.entities.Category;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.SellerProfile;
import com.uca.ecommerce.exceptions.FieldAlreadyExistsException;
import com.uca.ecommerce.exceptions.InvalidProductPatchException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.BrandRepository;
import com.uca.ecommerce.repository.CategoryRepository;
import com.uca.ecommerce.repository.ProductRepository;
import com.uca.ecommerce.repository.SellerProfileRepository;
import com.uca.ecommerce.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productMapper.toDtoList(productRepository.findAll());
    }

    @Override
    public ProductResponse getProductById(UUID id) {
        return productMapper.toDto(
                productRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Product not found"))
        );
    }

    @Override
    public ProductResponse createProduct(CreateProductRequest request) {
        if (productRepository.existsBySku(request.getSku())) {
            throw new FieldAlreadyExistsException("Product SKU already exists");
        }

        if (productRepository.existsBySlug(request.getSlug())) {
            throw new FieldAlreadyExistsException("Product slug already exists");
        }

        SellerProfile seller = sellerProfileRepository.findById(request.getSellerId())
                .orElseThrow(() -> new NotFoundException("Seller profile not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new NotFoundException("Brand not found"));

        return productMapper.toDto(
                productRepository.save(
                        productMapper.toEntityCreate(request, seller, category, brand)
                )
        );
    }

    @Override
    public ProductResponse updateProduct(UpdateProductRequest request, UUID id) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (!existing.getSku().equals(request.getSku())
                && productRepository.existsBySku(request.getSku())) {
            throw new FieldAlreadyExistsException("Product SKU already exists");
        }

        if (!existing.getSlug().equals(request.getSlug())
                && productRepository.existsBySlug(request.getSlug())) {
            throw new FieldAlreadyExistsException("Product slug already exists");
        }

        SellerProfile seller = sellerProfileRepository.findById(request.getSellerId())
                .orElseThrow(() -> new NotFoundException("Seller profile not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new NotFoundException("Brand not found"));

        return productMapper.toDto(
                productRepository.save(
                        productMapper.toEntityUpdate(request, existing, seller, category, brand)
                )
        );
    }

    @Override
    public ProductResponse patchProduct(PatchProductRequest request, UUID id) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (Boolean.TRUE.equals(request.getRemoveDescription())
                && request.getDescription() != null) {
            throw new InvalidProductPatchException(
                    "Description cannot be provided when removeDescription is true"
            );
        }

        if (Boolean.TRUE.equals(request.getRemoveConditionScore())
                && request.getConditionScore() != null) {
            throw new InvalidProductPatchException(
                    "Condition score cannot be provided when removeConditionScore is true"
            );
        }

        if (request.getSku() != null
                && !existing.getSku().equals(request.getSku())
                && productRepository.existsBySku(request.getSku())) {
            throw new FieldAlreadyExistsException("Product SKU already exists");
        }

        if (request.getSlug() != null
                && !existing.getSlug().equals(request.getSlug())
                && productRepository.existsBySlug(request.getSlug())) {
            throw new FieldAlreadyExistsException("Product slug already exists");
        }

        SellerProfile seller = request.getSellerId() == null
                ? null
                : sellerProfileRepository.findById(request.getSellerId())
                .orElseThrow(() -> new NotFoundException("Seller profile not found"));

        Category category = request.getCategoryId() == null
                ? null
                : categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Brand brand = request.getBrandId() == null
                ? null
                : brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new NotFoundException("Brand not found"));

        return productMapper.toDto(
                productRepository.save(
                        productMapper.toEntityPatch(request, existing, seller, category, brand)
                )
        );
    }

    @Override
    public ProductResponse deleteProduct(UUID id) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        productRepository.deleteById(id);

        return productMapper.toDto(existing);
    }
}