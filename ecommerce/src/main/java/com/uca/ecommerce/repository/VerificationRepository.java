package com.uca.ecommerce.repository;

import com.uca.ecommerce.domain.entities.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VerificationRepository extends JpaRepository<Verification, Long> {

    List<Verification> findByProductId(UUID productId);
    List<Verification> findByVerifiedByUuid(UUID verifiedByUuid);
}