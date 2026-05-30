package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.mappers.SellerProfileMapper;
import com.uca.ecommerce.domain.dto.request.sellerProfile.CreateSellerProfileRequest;
import com.uca.ecommerce.domain.dto.request.sellerProfile.UpdateSellerProfileRequest;
import com.uca.ecommerce.domain.dto.response.SellerProfileResponse;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.exceptions.StoreNameAlreadyExistsException;
import com.uca.ecommerce.exceptions.UserNotFoundException;
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
                .orElseThrow(() -> new UserNotFoundException("Seller profile not found")));
    }

    @Override
    public SellerProfileResponse createSellerProfile(CreateSellerProfileRequest request) {
        if (sellerProfileRepository.existsByStoreName(request.getStoreName()))
            throw new StoreNameAlreadyExistsException("Store name already exists");

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return sellerProfileMapper.toDto(
                sellerProfileRepository.save(sellerProfileMapper.toEntityCreate(request, user))
        );
    }

    @Override
    @Transactional
    public SellerProfileResponse updateSellerProfile(UpdateSellerProfileRequest request, UUID id) {
        this.getSellerProfileId(id);

        if (sellerProfileRepository.existsByStoreName(request.getStoreName()))
            throw new StoreNameAlreadyExistsException("Store name already exists");

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return sellerProfileMapper.toDto(
                sellerProfileRepository.save(sellerProfileMapper.toEntityUpdate(request, id, user))
        );
    }

    @Override
    public SellerProfileResponse deleteSellerProfile(UUID id) {
        SellerProfileResponse existing = this.getSellerProfileId(id);
        sellerProfileRepository.deleteById(id);
        return existing;
    }
}