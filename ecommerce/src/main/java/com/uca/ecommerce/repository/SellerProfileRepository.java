package com.uca.ecommerce.repository;

import com.uca.ecommerce.domain.entities.SellerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SellerProfileRepository extends JpaRepository<SellerProfile, UUID> {
    boolean existsByStoreName(String storeName);
    boolean existsByStoreNameAndIdNot(String storeName, UUID id);
}
