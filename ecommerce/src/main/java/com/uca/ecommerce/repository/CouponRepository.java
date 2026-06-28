package com.uca.ecommerce.repository;

import com.uca.ecommerce.domain.entities.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    boolean existsByCode(String code);
    Optional<Coupon> findByCode(String code);
    List<Coupon> findByOwner_Id(UUID ownerId);
    List<Coupon> findByOwnerIsNullOrderByCreatedAtDesc();
}
