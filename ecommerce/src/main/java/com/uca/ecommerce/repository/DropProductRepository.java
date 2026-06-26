package com.uca.ecommerce.repository;

import com.uca.ecommerce.domain.entities.DropProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DropProductRepository extends JpaRepository<DropProduct, UUID> {
    List<DropProduct> findByDropId(UUID dropId);
    boolean existsByDropIdAndProductId(UUID dropId, UUID productId);
    long countByProductId(UUID productId);
}
