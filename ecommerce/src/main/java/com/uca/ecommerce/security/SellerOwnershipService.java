package com.uca.ecommerce.security;


import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.SellerProfile;
import com.uca.ecommerce.repository.SellerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SellerOwnershipService {

    private final SellerProfileRepository sellerProfileRepository;

    public void validateSellerOwnsProduct(Product product) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) return;

        String email = auth.getName();

        SellerProfile seller = sellerProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Seller profile not found"
                ));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to modify this product"
            );
        }
    }
}