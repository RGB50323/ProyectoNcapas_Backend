package com.uca.ecommerce.repository;

import com.uca.ecommerce.domain.entities.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {

    List<ProductImage> findByProductId(UUID productId);

    boolean existsByProductIdAndUrl(UUID productId, String url);

    List<ProductImage> findByProductIdAndPrimaryImageTrue(UUID productId);

    void deleteByProductId(UUID productId);
}