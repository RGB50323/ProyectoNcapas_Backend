package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.Enums.Role;
import com.uca.ecommerce.common.mappers.DropProductMapper;
import com.uca.ecommerce.domain.dto.request.dropProduct.CreateDropProductRequest;
import com.uca.ecommerce.domain.dto.response.DropProductResponse;
import com.uca.ecommerce.domain.entities.Drop;
import com.uca.ecommerce.domain.entities.DropProduct;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.SellerProfile;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.exceptions.FieldAlreadyExistsException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.DropProductRepository;
import com.uca.ecommerce.repository.DropRepository;
import com.uca.ecommerce.repository.ProductRepository;
import com.uca.ecommerce.repository.SellerProfileRepository;
import com.uca.ecommerce.security.CurrentUserProvider;
import com.uca.ecommerce.services.DropProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DropProductServiceImpl implements DropProductService {

    private final DropProductRepository dropProductRepository;
    private final DropRepository dropRepository;
    private final ProductRepository productRepository;
    private final DropProductMapper dropProductMapper;
    private final SellerProfileRepository sellerProfileRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public List<DropProductResponse> getAllDropProducts() {
        return dropProductMapper.toDtoList(dropProductRepository.findAll());
    }

    @Override
    public List<DropProductResponse> getDropProductsByDropId(UUID dropId) {
        if (!dropRepository.existsById(dropId)) {
            throw new NotFoundException("Drop not found");
        }

        List<DropProduct> dropProducts = dropProductRepository.findByDropId(dropId);
        return dropProductMapper.toDtoList(dropProducts);
    }

    @Override
    public DropProductResponse createDropProduct(CreateDropProductRequest request) {
        Drop drop = dropRepository.findById(request.getDropId())
                .orElseThrow(() -> new NotFoundException("Drop not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        validateSellerCanAttach(drop, product);

        if (dropProductRepository.existsByDropIdAndProductId(request.getDropId(), request.getProductId())) {
            throw new FieldAlreadyExistsException("Product is already assigned to this drop");
        }

        DropProduct saved = dropProductRepository.save(
                dropProductMapper.toEntityCreate(drop, product));
        return dropProductMapper.toDto(saved);
    }

    @Override
    public DropProductResponse deleteDropProduct(UUID id) {
        DropProduct existing = dropProductRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Drop product not found"));

        validateSellerOwnsDrop(existing.getDrop());

        dropProductRepository.deleteById(id);
        return dropProductMapper.toDto(existing);
    }

    // el seller solo adjunta sus productos a sus drops, el admin puede todo
    private void validateSellerCanAttach(Drop drop, Product product) {
        if (isCurrentUserAdmin()) {
            return;
        }

        SellerProfile currentSeller = getCurrentSellerProfile();
        requireDropOwnedBy(drop, currentSeller);

        if (product.getSeller() == null || !product.getSeller().getId().equals(currentSeller.getId())) {
            throw new AccessDeniedException("You can only attach your own products");
        }
    }

    private void validateSellerOwnsDrop(Drop drop) {
        if (isCurrentUserAdmin()) {
            return;
        }

        SellerProfile currentSeller = getCurrentSellerProfile();
        requireDropOwnedBy(drop, currentSeller);
    }

    private void requireDropOwnedBy(Drop drop, SellerProfile seller) {
        if (drop.getOwner() == null || !drop.getOwner().getId().equals(seller.getId())) {
            throw new AccessDeniedException("You are not allowed to manage this drop");
        }
    }

    private SellerProfile getCurrentSellerProfile() {
        User currentUser = currentUserProvider.getCurrentUser();
        if (currentUser.getRole() != Role.SELLER) {
            throw new AccessDeniedException("Only sellers can manage seller-owned drops");
        }
        return sellerProfileRepository.findByUserEmail(currentUser.getEmail())
                .orElseThrow(() -> new AccessDeniedException("Seller profile not found"));
    }

    private boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
