package com.uca.ecommerce.repository;

import com.uca.ecommerce.common.Enums.AuthStatus;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    boolean existsBySku(String sku);

    boolean existsBySlug(String slug);

    List<Product> findBySellerId(UUID sellerId);

    long countByCategory_Id(UUID categoryId);

    long countByCategory_IdAndAuthStatus(UUID categoryId, AuthStatus authStatus);
}