package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.Enums.Role;
import com.uca.ecommerce.common.mappers.AuthMapper;
import com.uca.ecommerce.common.mappers.SellerProfileMapper;
import com.uca.ecommerce.domain.dto.request.sellerProfile.CreateSellerProfileRequest;
import com.uca.ecommerce.domain.dto.request.sellerProfile.UpdateSellerProfileRequest;
import com.uca.ecommerce.domain.dto.response.AuthResponse;
import com.uca.ecommerce.domain.dto.response.SellerProfileResponse;
import com.uca.ecommerce.domain.entities.RefreshToken;
import com.uca.ecommerce.domain.entities.SellerProfile;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.exceptions.FieldAlreadyExistsException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.SellerProfileRepository;
import com.uca.ecommerce.repository.UserRepository;
import com.uca.ecommerce.security.CurrentUserProvider;
import com.uca.ecommerce.security.JwtService;
import com.uca.ecommerce.services.RefreshTokenService;
import com.uca.ecommerce.services.SellerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SellerProfileServiceImpl implements SellerProfileService {

    private final SellerProfileRepository sellerProfileRepository;
    private final SellerProfileMapper sellerProfileMapper;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthMapper authMapper;
    private final RefreshTokenService refreshTokenService;
    private final CurrentUserProvider currentUserProvider;

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtService.generateToken(
                user.getUuid(), user.getEmail(), user.getRole().name()
        );
        RefreshToken refreshToken = refreshTokenService.create(user);
        LocalDateTime expiresAt = jwtService.getExpiresAt(accessToken);
        return authMapper.toDto(user, accessToken, refreshToken.getToken(), expiresAt);
    }


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
    public AuthResponse createSellerProfile(CreateSellerProfileRequest request) {
        if (sellerProfileRepository.existsByStoreName(request.getStoreName()))
            throw new FieldAlreadyExistsException("Store name already exists");

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setRole(Role.SELLER);
        userRepository.save(user);

        sellerProfileRepository.save(sellerProfileMapper.toEntityCreate(request, user));
        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public SellerProfileResponse updateSellerProfile(UpdateSellerProfileRequest request, UUID id) {
        SellerProfile existing = sellerProfileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Seller profile not found"));

        User currentUser = currentUserProvider.getCurrentUser();
        if (!existing.getUser().getUuid().equals(currentUser.getUuid()) && currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("No tienes permiso para modificar este perfil");
        }

        if (sellerProfileRepository.existsByStoreNameAndIdNot(request.getStoreName(), id))
            throw new FieldAlreadyExistsException("Store name already exists");

        return sellerProfileMapper.toDto(
                sellerProfileRepository.save(sellerProfileMapper.toEntityUpdate(request, existing))
        );
    }

    @Override
    @Transactional
    public SellerProfileResponse setVerified(UUID id, boolean verified) {
        SellerProfile existing = sellerProfileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Seller profile not found"));
        existing.setVerified(verified);
        return sellerProfileMapper.toDto(sellerProfileRepository.save(existing));
    }

    @Override
    public AuthResponse deleteSellerProfile(UUID id) {
        SellerProfile existing = sellerProfileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Seller profile not found"));

        User currentUser = currentUserProvider.getCurrentUser();
        if (!existing.getUser().getUuid().equals(currentUser.getUuid()) && currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("No tienes permiso para eliminar este perfil");
        }

        User user = existing.getUser();
        user.setRole(Role.BUYER);
        userRepository.save(user);

        sellerProfileRepository.deleteById(id);
        return buildAuthResponse(user);
    }
}