package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.Enums.Role;
import com.uca.ecommerce.common.Enums.StoreRequestStatus;
import com.uca.ecommerce.common.mappers.StoreRequestMapper;
import com.uca.ecommerce.domain.dto.request.storeRequest.CreateStoreRequestRequest;
import com.uca.ecommerce.domain.dto.request.storeRequest.ReviewStoreRequestRequest;
import com.uca.ecommerce.domain.dto.response.StoreRequestResponse;
import com.uca.ecommerce.domain.entities.SellerProfile;
import com.uca.ecommerce.domain.entities.StoreRequest;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.exceptions.FieldAlreadyExistsException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.exceptions.StoreRequestException;
import com.uca.ecommerce.repository.SellerProfileRepository;
import com.uca.ecommerce.repository.StoreRequestRepository;
import com.uca.ecommerce.repository.UserRepository;
import com.uca.ecommerce.security.CurrentUserProvider;
import com.uca.ecommerce.services.StoreRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreRequestServiceImpl implements StoreRequestService {

    private final StoreRequestRepository storeRequestRepository;
    private final StoreRequestMapper storeRequestMapper;
    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final CurrentUserProvider currentUserProvider;

    @Value("${app.store-request.cooldown-days:30}")
    private int cooldownDays;

    @Override
    public StoreRequestResponse create(CreateStoreRequestRequest request) {
        User user = currentUserProvider.getCurrentUser();

        if (user.getPhone() == null || user.getPhone().isBlank())
            throw new StoreRequestException("Debes registrar tu teléfono antes de solicitar una tienda.");

        if (user.getRole() == Role.SELLER)
            throw new StoreRequestException("Ya tienes una tienda registrada.");

        if (storeRequestRepository.existsByUserUuidAndStatus(user.getUuid(), StoreRequestStatus.PENDIENTE))
            throw new StoreRequestException("Ya tienes una solicitud en revisión.");

        Optional<StoreRequest> last = storeRequestRepository.findFirstByUserUuidOrderByCreatedAtDesc(user.getUuid());
        if (last.isPresent() && last.get().getStatus() == StoreRequestStatus.RECHAZADA && last.get().getReviewedAt() != null) {
            LocalDateTime nextEligibleAt = last.get().getReviewedAt().plusDays(cooldownDays);
            if (LocalDateTime.now().isBefore(nextEligibleAt)) {
                long daysLeft = java.time.Duration.between(LocalDateTime.now(), nextEligibleAt).toDays() + 1;
                throw new StoreRequestException("Tu solicitud fue rechazada. Podrás volver a aplicar en " + daysLeft + " día(s).");
            }
        }

        StoreRequest saved = storeRequestRepository.save(storeRequestMapper.toEntityCreate(request, user));
        return storeRequestMapper.toDto(saved, cooldownDays);
    }

    @Override
    public StoreRequestResponse getMyLatest() {
        User user = currentUserProvider.getCurrentUser();
        return storeRequestRepository.findFirstByUserUuidOrderByCreatedAtDesc(user.getUuid())
                .map(r -> storeRequestMapper.toDto(r, cooldownDays))
                .orElse(null);
    }

    @Override
    public List<StoreRequestResponse> getAll() {
        return storeRequestMapper.toDtoList(storeRequestRepository.findAllByOrderByCreatedAtDesc(), cooldownDays);
    }

    @Override
    public List<StoreRequestResponse> getPending() {
        return storeRequestMapper.toDtoList(
                storeRequestRepository.findByStatusOrderByCreatedAtAsc(StoreRequestStatus.PENDIENTE), cooldownDays);
    }

    @Override
    @Transactional
    public StoreRequestResponse review(UUID id, ReviewStoreRequestRequest request) {
        StoreRequest storeRequest = storeRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Store request not found"));

        if (storeRequest.getStatus() != StoreRequestStatus.PENDIENTE)
            throw new StoreRequestException("Esta solicitud ya fue revisada.");

        if (request.getDecision() == StoreRequestStatus.PENDIENTE)
            throw new StoreRequestException("Decisión inválida.");

        User reviewer = currentUserProvider.getCurrentUser();
        storeRequest.setReviewedBy(reviewer);
        storeRequest.setReviewedAt(LocalDateTime.now());
        storeRequest.setReviewNote(request.getReviewNote());
        storeRequest.setStatus(request.getDecision());

        if (request.getDecision() == StoreRequestStatus.APROBADA) {
            if (sellerProfileRepository.existsByStoreName(storeRequest.getStoreName()))
                throw new FieldAlreadyExistsException("Store name already exists");

            User owner = storeRequest.getUser();
            owner.setRole(Role.SELLER);
            userRepository.save(owner);

            sellerProfileRepository.save(SellerProfile.builder()
                    .storeName(storeRequest.getStoreName())
                    .storeDescription(storeRequest.getStoreDescription())
                    .storeCategory(storeRequest.getStoreCategory())
                    .location(storeRequest.getLocation())
                    .user(owner)
                    .build());
        }

        return storeRequestMapper.toDto(storeRequestRepository.save(storeRequest), cooldownDays);
    }
}
