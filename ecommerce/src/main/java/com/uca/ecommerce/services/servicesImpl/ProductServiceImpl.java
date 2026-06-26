package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.Enums.AuthStatus;
import com.uca.ecommerce.common.Enums.Role;
import com.uca.ecommerce.common.mappers.ProductMapper;
import com.uca.ecommerce.domain.dto.request.product.CreateProductRequest;
import com.uca.ecommerce.domain.dto.request.product.PatchProductRequest;
import com.uca.ecommerce.domain.dto.request.product.UpdateProductRequest;
import com.uca.ecommerce.domain.dto.response.ProductResponse;
import com.uca.ecommerce.domain.entities.Brand;
import com.uca.ecommerce.domain.entities.Category;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.SellerProfile;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.exceptions.FieldAlreadyExistsException;
import com.uca.ecommerce.exceptions.InvalidProductPatchException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.exceptions.ProductHasActiveProcessesException;
import com.uca.ecommerce.repository.*;
import com.uca.ecommerce.security.CurrentUserProvider;
import com.uca.ecommerce.security.SellerOwnershipService;
import com.uca.ecommerce.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ProductImageRepository productImageRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductBadgeRepository productBadgeRepository;
    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final DropProductRepository dropProductRepository;
    private final WishlistRepository wishlistRepository;
    private final CartItemRepository cartItemRepository;
    private final VerificationRepository verificationRepository;
    private final StockAlertRepository stockAlertRepository;
    private final UserProductEventRepository userProductEventRepository;
    private final SellerOwnershipService sellerOwnershipService;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public List<ProductResponse> getAllProducts() {
        return productMapper.toDtoList(productRepository.findAll());
    }

    @Override
    public List<ProductResponse> getPublicProducts() {
        return productMapper.toDtoList(
                productRepository.findByAuthStatusAndTotalStockGreaterThanOrderByCreatedAtDesc(
                        AuthStatus.AUTHENTICATED,
                        0
                )
        );
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
        validateSellerCannotCreateAdminManagedFields(request);
        request.setTotalStock(0);

        if (productRepository.existsBySku(request.getSku())) {
            throw new FieldAlreadyExistsException("Product SKU already exists");
        }

        if (productRepository.existsBySlug(request.getSlug())) {
            throw new FieldAlreadyExistsException("Product slug already exists");
        }

        SellerProfile seller = resolveSellerForCreate(request.getSellerId());

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

        sellerOwnershipService.validateSellerOwnsProduct(existing);
        validateSellerCannotUpdateAdminManagedFields(request, existing);

        if (!existing.getSku().equals(request.getSku())
                && productRepository.existsBySku(request.getSku())) {
            throw new FieldAlreadyExistsException("Product SKU already exists");
        }

        if (!existing.getSlug().equals(request.getSlug())
                && productRepository.existsBySlug(request.getSlug())) {
            throw new FieldAlreadyExistsException("Product slug already exists");
        }

        SellerProfile seller = resolveSellerForUpdate(request.getSellerId(), existing.getSeller());

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new NotFoundException("Brand not found"));

        Product saved = productRepository.save(
                productMapper.toEntityUpdate(request, existing, seller, category, brand)
        );
        syncProductTotalStock(saved);

        return productMapper.toDto(saved);
    }

    @Override
    public ProductResponse patchProduct(PatchProductRequest request, UUID id) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        sellerOwnershipService.validateSellerOwnsProduct(existing);
        validateSellerCannotPatchAdminManagedFields(request, existing);

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

        SellerProfile seller = resolveSellerForPatch(request.getSellerId(), existing.getSeller());

        Category category = request.getCategoryId() == null
                ? null
                : categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not found"));

        Brand brand = request.getBrandId() == null
                ? null
                : brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new NotFoundException("Brand not found"));

        Product saved = productRepository.save(
                productMapper.toEntityPatch(request, existing, seller, category, brand)
        );
        syncProductTotalStock(saved);

        return productMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProductResponse deleteProduct(UUID id) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        sellerOwnershipService.validateSellerOwnsProduct(existing);
        validateProductHasNoActiveProcesses(id);

        productImageRepository.deleteByProductId(id);
        productVariantRepository.deleteByProductId(id);


        productRepository.deleteById(id);

        return productMapper.toDto(existing);
    }

    @Override
    public List<ProductResponse> getProductsBySellerEmail(String email) {
        SellerProfile seller = sellerProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new NotFoundException("Seller profile not found"));

        return productRepository.findBySellerId(seller.getId())
                .stream()
                .map(productMapper::toDto)
                .toList();
    }



    private void syncProductTotalStock(Product product) {
        Long totalStock = productVariantRepository.sumStockByProductId(product.getId());
        product.setTotalStock(totalStock == null ? 0 : totalStock.intValue());
        productRepository.save(product);
    }

    private void validateProductHasNoActiveProcesses(UUID productId) {
        boolean hasActiveProcesses = productBadgeRepository.countByProductId(productId) > 0
                || reviewRepository.countByProductId(productId) > 0
                || orderItemRepository.countByProductId(productId) > 0
                || dropProductRepository.countByProductId(productId) > 0
                || wishlistRepository.countByProduct_Id(productId) > 0
                || cartItemRepository.countByProduct_Id(productId) > 0
                || verificationRepository.countByProductId(productId) > 0
                || stockAlertRepository.countByProductId(productId) > 0
                || userProductEventRepository.countByProduct_Id(productId) > 0;

        if (hasActiveProcesses) {
            throw new ProductHasActiveProcessesException(
                    "Este producto tiene procesos activos y no puede eliminarse"
            );
        }
    }

    private SellerProfile resolveSellerForCreate(UUID requestedSellerId) {
        if (isCurrentUserAdmin()) {
            return findSellerOrThrow(requestedSellerId);
        }

        SellerProfile currentSeller = getCurrentSellerProfile();
        if (requestedSellerId != null && !requestedSellerId.equals(currentSeller.getId())) {
            throw new AccessDeniedException("You cannot create products for another seller");
        }
        return currentSeller;
    }

    private SellerProfile resolveSellerForUpdate(UUID requestedSellerId, SellerProfile existingSeller) {
        if (isCurrentUserAdmin()) {
            return findSellerOrThrow(requestedSellerId);
        }

        SellerProfile currentSeller = getCurrentSellerProfile();
        if (!currentSeller.getId().equals(existingSeller.getId())) {
            throw new AccessDeniedException("You are not allowed to modify this product");
        }
        if (requestedSellerId != null && !requestedSellerId.equals(existingSeller.getId())) {
            throw new AccessDeniedException("Only admins can reassign products to another seller");
        }
        return existingSeller;
    }

    private SellerProfile resolveSellerForPatch(UUID requestedSellerId, SellerProfile existingSeller) {
        if (requestedSellerId == null) {
            return null;
        }
        return resolveSellerForUpdate(requestedSellerId, existingSeller);
    }

    private SellerProfile getCurrentSellerProfile() {
        User currentUser = currentUserProvider.getCurrentUser();
        if (currentUser.getRole() != Role.SELLER) {
            throw new AccessDeniedException("Only sellers can manage seller-owned products");
        }
        return sellerProfileRepository.findByUserEmail(currentUser.getEmail())
                .orElseThrow(() -> new AccessDeniedException("Seller profile not found"));
    }

    private SellerProfile findSellerOrThrow(UUID sellerId) {
        return sellerProfileRepository.findById(sellerId)
                .orElseThrow(() -> new NotFoundException("Seller profile not found"));
    }


    private void validateSellerCannotCreateAdminManagedFields(CreateProductRequest request) {
        if (isCurrentUserAdmin()) return;

        boolean triesToSetProductBadgeFlags = Boolean.TRUE.equals(request.getFeatured())
                || Boolean.TRUE.equals(request.getNewProduct())
                || Boolean.TRUE.equals(request.getLimited())
                || Boolean.TRUE.equals(request.getPrivateDrop());

        boolean triesToSetAuthStatus = request.getAuthStatus() != null
                && request.getAuthStatus() != com.uca.ecommerce.common.Enums.AuthStatus.NOT_SUBMITTED;

        if (triesToSetProductBadgeFlags || triesToSetAuthStatus) {
            throw new AccessDeniedException("Only admins can set product badge flags or authentication status");
        }

        request.setAuthStatus(com.uca.ecommerce.common.Enums.AuthStatus.NOT_SUBMITTED);
        request.setFeatured(false);
        request.setNewProduct(false);
        request.setLimited(false);
        request.setPrivateDrop(false);
    }

    private void validateSellerCannotUpdateAdminManagedFields(UpdateProductRequest request, Product existing) {
        if (isCurrentUserAdmin()) return;

        boolean changesProductBadgeFlags = !request.getFeatured().equals(existing.isFeatured())
                || !request.getNewProduct().equals(existing.isNewProduct())
                || !request.getLimited().equals(existing.isLimited())
                || !request.getPrivateDrop().equals(existing.isPrivateDrop());

        if (changesProductBadgeFlags || !isSellerAllowedAuthTransition(existing, request.getAuthStatus())) {
            throw new AccessDeniedException("Only admins can change product badge flags or review authentication status");
        }
    }

    private void validateSellerCannotPatchAdminManagedFields(PatchProductRequest request, Product existing) {
        if (isCurrentUserAdmin()) return;

        boolean triesToPatchProductBadgeFlags = request.getFeatured() != null
                || request.getNewProduct() != null
                || request.getLimited() != null
                || request.getPrivateDrop() != null;

        if (triesToPatchProductBadgeFlags || !isSellerAllowedAuthTransition(existing, request.getAuthStatus())) {
            throw new AccessDeniedException("Only admins can change product badge flags or review authentication status");
        }
    }

    private boolean isSellerAllowedAuthTransition(Product existing, com.uca.ecommerce.common.Enums.AuthStatus requestedStatus) {
        if (requestedStatus == null || requestedStatus == existing.getAuthStatus()) return true;

        return requestedStatus == com.uca.ecommerce.common.Enums.AuthStatus.PENDING
                && existing.getAuthStatus() != com.uca.ecommerce.common.Enums.AuthStatus.AUTHENTICATED;
    }

    private boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

}
