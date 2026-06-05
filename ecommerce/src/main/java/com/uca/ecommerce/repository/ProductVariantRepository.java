package com.uca.ecommerce.repository;

import com.uca.ecommerce.domain.entities.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

    List<ProductVariant> findByProductId(UUID productId);

    boolean existsByProductIdAndSizeAndColorName(UUID productId, String size, String colorName);
}