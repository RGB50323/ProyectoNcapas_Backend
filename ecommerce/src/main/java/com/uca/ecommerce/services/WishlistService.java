package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.wishlist.CreateWishlistRequest;
import com.uca.ecommerce.domain.dto.response.WishlistResponse;

import java.util.List;
import java.util.UUID;

public interface WishlistService {

    List<WishlistResponse> getMyWishlist();

    WishlistResponse addToWishlist(CreateWishlistRequest request);

    WishlistResponse removeFromWishlist(UUID id);
}
