package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.Enums.CartSessionStatus;
import com.uca.ecommerce.common.Enums.ProductEventType;
import com.uca.ecommerce.common.mappers.CartItemMapper;
import com.uca.ecommerce.domain.dto.request.cartItem.CreateCartItemRequest;
import com.uca.ecommerce.domain.dto.request.cartItem.UpdateCartItemRequest;
import com.uca.ecommerce.domain.dto.response.CartItemResponse;
import com.uca.ecommerce.domain.entities.*;
import com.uca.ecommerce.exceptions.InsufficientStockException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.CartItemRepository;
import com.uca.ecommerce.repository.CartSessionRepository;
import com.uca.ecommerce.repository.ProductRepository;
import com.uca.ecommerce.repository.ProductVariantRepository;
import com.uca.ecommerce.security.CurrentUserProvider;
import com.uca.ecommerce.services.CartItemService;
import com.uca.ecommerce.services.UserProductEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final CartSessionRepository cartSessionRepository;

    @Override
    public List<CartItemResponse> getMyCart() {
        User user = currentUserProvider.getCurrentUser();
        return cartItemMapper.toDtoList(cartItemRepository.findByUser_Uuid(user.getUuid()));
    }

    @Override
    public CartItemResponse addToCart(CreateCartItemRequest request) {
        User user = currentUserProvider.getCurrentUser();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new NotFoundException("Product variant not found"));

        if (!variant.getProduct().getId().equals(product.getId()))
            throw new NotFoundException("Variant does not belong to the given product");

        Optional<CartItem> existing = cartItemRepository.findByUser_UuidAndProduct_IdAndVariant_Id(
                user.getUuid(), product.getId(), variant.getId());

        int requestedQty = request.getQuantity();
        int currentQty = existing.map(CartItem::getQuantity).orElse(0);
        int totalQty = currentQty + requestedQty;

        if (totalQty > variant.getStock())
            throw new InsufficientStockException(
                    "Stock insuficiente. Disponible: " + variant.getStock() + ", en tu carrito: " + currentQty);

        Optional<CartSession> activeSession = cartSessionRepository
                .findByUserUuidAndStatus(user.getUuid(), CartSessionStatus.ACTIVE);

        if (activeSession.isEmpty()) {
            cartSessionRepository.findByUserUuidAndStatusAndAbandonedManually(
                            user.getUuid(), CartSessionStatus.ABANDONED, false)
                    .ifPresentOrElse(
                            session -> {
                                session.setStatus(CartSessionStatus.ACTIVE);
                                session.setAbandonedAt(null);
                                session.setAbandonedManually(false);
                                cartSessionRepository.save(session);
                            },
                            () -> cartSessionRepository.save(
                                    CartSession.builder()
                                            .user(user)
                                            .status(CartSessionStatus.ACTIVE)
                                            .build()
                            )
                    );
        }

        CartItem cartItem;
        if (existing.isPresent()) {
            cartItem = existing.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItem.setAddedAt(LocalDateTime.now());
        } else {
            cartItem = cartItemMapper.toEntityCreate(request, user, product, variant);
        }

        CartItem saved = cartItemRepository.save(cartItem);
        userProductEventService.registerCurrentBuyerEvent(product.getId(), ProductEventType.ADD_TO_CART);
        return cartItemMapper.toDto(saved);
    }

    @Override
    public CartItemResponse updateCartItem(UpdateCartItemRequest request, UUID id) {
        CartItem cartItem = getOwnedCartItem(id);

        ProductVariant variant = cartItem.getVariant();
        if (request.getQuantity() > variant.getStock())
            throw new InsufficientStockException("Stock insuficiente. Disponible: " + variant.getStock());

        cartSessionRepository.findByUserUuidAndStatusAndAbandonedManually(
                        cartItem.getUser().getUuid(), CartSessionStatus.ABANDONED, false)
                .ifPresent(session -> {
                    session.setStatus(CartSessionStatus.ACTIVE);
                    session.setAbandonedAt(null);
                    cartSessionRepository.save(session);
                });

        cartItem.setQuantity(request.getQuantity());
        cartItem.setAddedAt(LocalDateTime.now());

        return cartItemMapper.toDto(cartItemRepository.save(cartItem));
    }

    @Override
    public CartItemResponse removeFromCart(UUID id) {
        CartItem cartItem = getOwnedCartItem(id);
        cartItemRepository.deleteById(id);

        User user = cartItem.getUser();
        List<CartItem> remaining = cartItemRepository.findByUser_Uuid(user.getUuid());
        if (remaining.isEmpty()) {
            cartSessionRepository.findByUserUuidAndStatus(user.getUuid(), CartSessionStatus.ACTIVE)
                    .ifPresent(session -> {
                        session.setStatus(CartSessionStatus.ABANDONED);
                        session.setAbandonedAt(LocalDateTime.now());
                        session.setAbandonedManually(true);
                        cartSessionRepository.save(session);
                    });
        }

        return cartItemMapper.toDto(cartItem);
    }

    private CartItem getOwnedCartItem(UUID id) {
        User user = currentUserProvider.getCurrentUser();
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cart item not found"));

        if (!cartItem.getUser().getUuid().equals(user.getUuid()))
            throw new NotFoundException("Cart item not found");

        return cartItem;
    }
}