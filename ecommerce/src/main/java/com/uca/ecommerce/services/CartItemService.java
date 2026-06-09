package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.cartItem.CreateCartItemRequest;
import com.uca.ecommerce.domain.dto.request.cartItem.UpdateCartItemRequest;
import com.uca.ecommerce.domain.dto.response.CartItemResponse;

import java.util.List;
import java.util.UUID;

public interface CartItemService {

    List<CartItemResponse> getMyCart();

    CartItemResponse addToCart(CreateCartItemRequest request);

    CartItemResponse updateCartItem(UpdateCartItemRequest request, UUID id);

    CartItemResponse removeFromCart(UUID id);
}
