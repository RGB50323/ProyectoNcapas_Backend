package com.uca.ecommerce.repository;

import com.uca.ecommerce.domain.entities.ProductBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductBadgeRepository extends JpaRepository<ProductBadge, UUID> {
    List<ProductBadge> findByProductId(UUID productId);
    boolean existsByProductIdAndLabelIgnoreCase(UUID productId, String label);
}