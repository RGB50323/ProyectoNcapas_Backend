package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.mappers.WishlistMapper;
import com.uca.ecommerce.domain.dto.request.wishlist.CreateWishlistRequest;
import com.uca.ecommerce.domain.dto.response.WishlistResponse;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.domain.entities.Wishlist;
import com.uca.ecommerce.exceptions.FieldAlreadyExistsException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.ProductRepository;
import com.uca.ecommerce.repository.WishlistRepository;
import com.uca.ecommerce.security.CurrentUserProvider;
import com.uca.ecommerce.services.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final WishlistMapper wishlistMapper;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public List<WishlistResponse> getMyWishlist() {
        User user = currentUserProvider.getCurrentUser();
        List<Wishlist> wishlist = wishlistRepository.findByUser_Uuid(user.getUuid());
        return wishlistMapper.toDtoList(wishlist);
    }

    @Override
    public WishlistResponse addToWishlist(CreateWishlistRequest request) {
        User user = currentUserProvider.getCurrentUser();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (wishlistRepository.existsByUser_UuidAndProduct_Id(user.getUuid(), product.getId())) {
            throw new FieldAlreadyExistsException("Product is already in the wishlist");
        }

        Wishlist saved = wishlistRepository.save(wishlistMapper.toEntityCreate(user, product));
        return wishlistMapper.toDto(saved);
    }

    @Override
    public WishlistResponse removeFromWishlist(UUID id) {
        User user = currentUserProvider.getCurrentUser();

        Wishlist wishlist = wishlistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Wishlist item not found"));

        if (!wishlist.getUser().getUuid().equals(user.getUuid())) {
            throw new NotFoundException("Wishlist item not found");
        }

        wishlistRepository.deleteById(id);
        return wishlistMapper.toDto(wishlist);
    }
}
