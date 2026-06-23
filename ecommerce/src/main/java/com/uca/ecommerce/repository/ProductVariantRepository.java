package com.uca.ecommerce.repository;

import com.uca.ecommerce.common.Enums.AuthStatus;
import com.uca.ecommerce.domain.entities.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

    List<ProductVariant> findByProductId(UUID productId);

    List<ProductVariant> findByProduct_AuthStatusAndProduct_TotalStockGreaterThan(
            AuthStatus authStatus,
            Integer totalStock
    );

    boolean existsByProductIdAndSizeAndColorName(UUID productId, String size, String colorName);

    @Query("select coalesce(sum(variant.stock), 0) from ProductVariant variant where variant.product.id = :productId")
    Long sumStockByProductId(@Param("productId") UUID productId);

    void deleteByProductId(UUID productId);
}