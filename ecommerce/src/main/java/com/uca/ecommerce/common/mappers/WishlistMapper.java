package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.response.WishlistResponse;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.domain.entities.Wishlist;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WishlistMapper {

    public Wishlist toEntityCreate(User user, Product product) {
        return Wishlist.builder()
                .user(user)
                .product(product)
                .build();
    }

    public WishlistResponse toDto(Wishlist wishlist) {
        return WishlistResponse.builder()
                .id(wishlist.getId())
                .userId(wishlist.getUser().getUuid())
                .productId(wishlist.getProduct().getId())
                .productName(wishlist.getProduct().getName())
                .productSku(wishlist.getProduct().getSku())
                .addedAt(wishlist.getAddedAt())
                .build();
    }

    public List<WishlistResponse> toDtoList(List<Wishlist> wishlists) {
        return wishlists.stream()
                .map(this::toDto)
                .toList();
    }
}
