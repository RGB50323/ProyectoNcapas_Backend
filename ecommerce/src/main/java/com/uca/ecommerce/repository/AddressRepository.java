package com.uca.ecommerce.repository;

import com.uca.ecommerce.domain.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {
    List<Address> findByUserUuid(UUID userId);
    boolean existsByUserUuidAndIsDefaultTrue(UUID userId);
    Optional<Address> findByUserUuidAndIsDefaultTrue(UUID userId);
    void deleteAllByUserUuid(UUID userId);
}
