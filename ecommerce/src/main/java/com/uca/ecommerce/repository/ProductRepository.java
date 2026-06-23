package com.uca.ecommerce.repository;

import com.uca.ecommerce.common.Enums.AuthStatus;
import com.uca.ecommerce.domain.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    boolean existsBySku(String sku);

    boolean existsBySlug(String slug);

    List<Product> findBySellerId(UUID sellerId);

    long countByCategory_Id(UUID categoryId);

    long countByCategory_IdAndAuthStatus(UUID categoryId, AuthStatus authStatus);

    List<Product> findByAuthStatusAndTotalStockGreaterThanOrderByCreatedAtDesc(
            AuthStatus authStatus,
            Integer totalStock
    );

    List<Product> findTop12ByAuthStatusAndTotalStockGreaterThanOrderByCreatedAtDesc(
            AuthStatus authStatus,
            Integer totalStock
    );

    List<Product> findByAuthStatusOrderByCreatedAtDesc(AuthStatus authStatus);
}
