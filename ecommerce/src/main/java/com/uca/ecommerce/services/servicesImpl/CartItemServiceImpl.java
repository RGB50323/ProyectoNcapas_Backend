package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.Enums.ProductEventType;
import com.uca.ecommerce.common.mappers.CartItemMapper;
import com.uca.ecommerce.domain.dto.request.cartItem.CreateCartItemRequest;
import com.uca.ecommerce.domain.dto.request.cartItem.UpdateCartItemRequest;
import com.uca.ecommerce.domain.dto.response.CartItemResponse;
import com.uca.ecommerce.domain.entities.CartItem;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.ProductVariant;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.CartItemRepository;
import com.uca.ecommerce.repository.ProductRepository;
import com.uca.ecommerce.repository.ProductVariantRepository;
import com.uca.ecommerce.security.CurrentUserProvider;
import com.uca.ecommerce.services.CartItemService;
import com.uca.ecommerce.services.UserProductEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CartItemMapper cartItemMapper;
    private final CurrentUserProvider currentUserProvider;
    private final UserProductEventService userProductEventService;

    @Override
    public List<CartItemResponse> getMyCart() {
        User user = currentUserProvider.getCurrentUser();
        List<CartItem> cart = cartItemRepository.findByUser_Uuid(user.getUuid());
        return cartItemMapper.toDtoList(cart);
    }

    @Override
    public CartItemResponse addToCart(CreateCartItemRequest request) {
        User user = currentUserProvider.getCurrentUser();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new NotFoundException("Product variant not found"));

        if (!variant.getProduct().getId().equals(product.getId())) {
            throw new NotFoundException("Variant does not belong to the given product");
        }

        // Merge with existing line instead of duplicating it
        Optional<CartItem> existing = cartItemRepository.findByUser_UuidAndProduct_IdAndVariant_Id(
                user.getUuid(), product.getId(), variant.getId());

        CartItem cartItem;
        if (existing.isPresent()) {
            cartItem = existing.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            cartItem = cartItemMapper.toEntityCreate(request, user, product, variant);
        }

        CartItem saved = cartItemRepository.save(cartItem);
        userProductEventService.registerCurrentBuyerEvent(
                product.getId(),
                ProductEventType.ADD_TO_CART
        );
        return cartItemMapper.toDto(saved);
    }

    @Override
    public CartItemResponse updateCartItem(UpdateCartItemRequest request, UUID id) {
        CartItem cartItem = getOwnedCartItem(id);
        cartItem.setQuantity(request.getQuantity());

        CartItem saved = cartItemRepository.save(cartItem);
        return cartItemMapper.toDto(saved);
    }

    @Override
    public CartItemResponse removeFromCart(UUID id) {
        CartItem cartItem = getOwnedCartItem(id);
        cartItemRepository.deleteById(id);
        return cartItemMapper.toDto(cartItem);
    }

    // Returns the cart item only if it belongs to the current user, hiding existence otherwise
    private CartItem getOwnedCartItem(UUID id) {
        User user = currentUserProvider.getCurrentUser();
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cart item not found"));

        if (!cartItem.getUser().getUuid().equals(user.getUuid())) {
            throw new NotFoundException("Cart item not found");
        }

        return cartItem;
    }
}
