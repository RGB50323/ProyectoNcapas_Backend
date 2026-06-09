package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.request.cartItem.CreateCartItemRequest;
import com.uca.ecommerce.domain.dto.request.cartItem.UpdateCartItemRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.CartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cart-items")
@RequiredArgsConstructor
public class CartItemController extends BaseController {

    private final CartItemService cartItemService;

    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getMyCart() {
        return buildResponse(
                "Cart retrieved successfully",
                HttpStatus.OK,
                cartItemService.getMyCart()
        );
    }

    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> addToCart(@Valid @RequestBody CreateCartItemRequest request) {
        return buildResponse(
                "Product added to cart successfully",
                HttpStatus.CREATED,
                cartItemService.addToCart(request)
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GeneralResponse> updateCartItem(@Valid @RequestBody UpdateCartItemRequest request, @PathVariable UUID id) {
        return buildResponse(
                "Cart item updated successfully",
                HttpStatus.OK,
                cartItemService.updateCartItem(request, id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> removeFromCart(@PathVariable UUID id) {
        return buildResponse(
                "Product removed from cart successfully",
                HttpStatus.OK,
                cartItemService.removeFromCart(id)
        );
    }
}
