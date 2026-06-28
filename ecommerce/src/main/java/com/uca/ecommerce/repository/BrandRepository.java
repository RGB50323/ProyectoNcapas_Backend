package com.uca.ecommerce.repository;

import com.uca.ecommerce.domain.entities.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {

    boolean existsByName(String name);
    boolean existsBySlug(String slug);
}
