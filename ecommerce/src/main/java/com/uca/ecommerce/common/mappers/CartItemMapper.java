package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.request.cartItem.CreateCartItemRequest;
import com.uca.ecommerce.domain.dto.response.CartItemResponse;
import com.uca.ecommerce.domain.entities.CartItem;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.ProductVariant;
import com.uca.ecommerce.domain.entities.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CartItemMapper {

    public CartItem toEntityCreate(CreateCartItemRequest request, User user, Product product, ProductVariant variant) {
        return CartItem.builder()
                .user(user)
                .product(product)
                .variant(variant)
                .quantity(request.getQuantity())
                .build();
    }

    public CartItemResponse toDto(CartItem cartItem) {
        BigDecimal unitPrice = cartItem.getProduct().getPrice()
                .add(cartItem.getVariant().getPriceDelta());
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return CartItemResponse.builder()
                .id(cartItem.getId())
                .userId(cartItem.getUser().getUuid())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .productSku(cartItem.getProduct().getSku())
                .variantId(cartItem.getVariant().getId())
                .variantSize(cartItem.getVariant().getSize())
                .variantColorName(cartItem.getVariant().getColorName())
                .variantColorHex(cartItem.getVariant().getColorHex())
                .quantity(cartItem.getQuantity())
                .unitPrice(unitPrice)
                .lineTotal(lineTotal)
                .addedAt(cartItem.getAddedAt())
                .build();
    }

    public List<CartItemResponse> toDtoList(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::toDto)
                .toList();
    }
}
