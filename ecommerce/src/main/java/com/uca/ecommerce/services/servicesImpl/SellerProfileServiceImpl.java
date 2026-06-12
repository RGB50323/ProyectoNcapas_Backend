package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.Enums.Role;
import com.uca.ecommerce.common.mappers.SellerProfileMapper;
import com.uca.ecommerce.domain.dto.request.sellerProfile.CreateSellerProfileRequest;
import com.uca.ecommerce.domain.dto.request.sellerProfile.UpdateSellerProfileRequest;
import com.uca.ecommerce.domain.dto.response.SellerProfileResponse;
import com.uca.ecommerce.domain.entities.SellerProfile;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.exceptions.FieldAlreadyExistsException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.SellerProfileRepository;
import com.uca.ecommerce.repository.UserRepository;
import com.uca.ecommerce.services.SellerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SellerProfileServiceImpl implements SellerProfileService {

    private final SellerProfileRepository sellerProfileRepository;
    private final SellerProfileMapper sellerProfileMapper;
    private final UserRepository userRepository;

    @Override
    public List<SellerProfileResponse> getAllSellerProfiles() {
        return sellerProfileMapper.toDtoList(sellerProfileRepository.findAll());
    }

    @Override
    public SellerProfileResponse getSellerProfileId(UUID id) {
        return sellerProfileMapper.toDto(sellerProfileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Seller profile not found")));
    }

    @Override
    public SellerProfileResponse createSellerProfile(CreateSellerProfileRequest request) {
        if (sellerProfileRepository.existsByStoreName(request.getStoreName()))
            throw new FieldAlreadyExistsException("Store name already exists");

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setRole(Role.SELLER);
        userRepository.save(user);

        return sellerProfileMapper.toDto(
                sellerProfileRepository.save(sellerProfileMapper.toEntityCreate(request, user))
        );
    }


    @Override
    @Transactional
    public SellerProfileResponse updateSellerProfile(UpdateSellerProfileRequest request, UUID id) {
        SellerProfile existing = sellerProfileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Seller profile not found"));

        if (sellerProfileRepository.existsByStoreNameAndIdNot(request.getStoreName(), id))
            throw new FieldAlreadyExistsException("Store name already exists");

        return sellerProfileMapper.toDto(
                sellerProfileRepository.save(sellerProfileMapper.toEntityUpdate(request, existing))
        );
    }

    @Override
    public SellerProfileResponse deleteSellerProfile(UUID id) {
        SellerProfileResponse existing = this.getSellerProfileId(id);

        User user = userRepository.findById(existing.getUser().getUuid())
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setRole(Role.BUYER);
        userRepository.save(user);

        sellerProfileRepository.deleteById(id);
        return existing;
    }
}