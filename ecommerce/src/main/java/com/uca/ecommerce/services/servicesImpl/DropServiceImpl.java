package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.Enums.Role;
import com.uca.ecommerce.common.mappers.DropMapper;
import com.uca.ecommerce.domain.dto.request.drop.CreateDropRequest;
import com.uca.ecommerce.domain.dto.request.drop.PatchDropRequest;
import com.uca.ecommerce.domain.dto.request.drop.UpdateDropRequest;
import com.uca.ecommerce.domain.dto.response.DropResponse;
import com.uca.ecommerce.domain.entities.Drop;
import com.uca.ecommerce.domain.entities.SellerProfile;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.exceptions.FieldAlreadyExistsException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.DropRepository;
import com.uca.ecommerce.repository.SellerProfileRepository;
import com.uca.ecommerce.security.CurrentUserProvider;
import com.uca.ecommerce.services.DropService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DropServiceImpl implements DropService {

    private final DropRepository dropRepository;
    private final DropMapper dropMapper;
    private final SellerProfileRepository sellerProfileRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public List<DropResponse> getAllDrops() {
        return dropMapper.toDtoList(dropRepository.findAll());
    }

    @Override
    public DropResponse getDropById(UUID id) {
        Drop drop = dropRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Drop not found"));
        return dropMapper.toDto(drop);
    }

    @Override
    public List<DropResponse> getMyDrops() {
        SellerProfile seller = getCurrentSellerProfile();
        return dropMapper.toDtoList(dropRepository.findByOwner_Id(seller.getId()));
    }

    @Override
    public DropResponse createDrop(CreateDropRequest request) {
        if (dropRepository.existsBySlug(request.getSlug())) {
            throw new FieldAlreadyExistsException("Drop slug already exists");
        }

        SellerProfile owner = resolveOwnerForCreate();

        Drop saved = dropRepository.save(dropMapper.toEntityCreate(request, owner));
        return dropMapper.toDto(saved);
    }

    @Override
    public DropResponse updateDrop(UpdateDropRequest request, UUID id) {
        Drop existing = dropRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Drop not found"));

        validateSellerOwnsDrop(existing);

        if (!existing.getSlug().equals(request.getSlug())
                && dropRepository.existsBySlug(request.getSlug())) {
            throw new FieldAlreadyExistsException("Drop slug already exists");
        }

        Drop saved = dropRepository.save(dropMapper.toEntityUpdate(request, existing));
        return dropMapper.toDto(saved);
    }

    @Override
    public DropResponse patchDrop(PatchDropRequest request, UUID id) {
        Drop existing = dropRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Drop not found"));

        validateSellerOwnsDrop(existing);

        if (request.getSlug() != null
                && !existing.getSlug().equals(request.getSlug())
                && dropRepository.existsBySlug(request.getSlug())) {
            throw new FieldAlreadyExistsException("Drop slug already exists");
        }

        Drop saved = dropRepository.save(dropMapper.toEntityPatch(request, existing));
        return dropMapper.toDto(saved);
    }

    @Override
    public DropResponse deleteDrop(UUID id) {
        Drop existing = dropRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Drop not found"));

        validateSellerOwnsDrop(existing);

        dropRepository.deleteById(id);
        return dropMapper.toDto(existing);
    }

    // admin crea drops globales sin dueno, el seller queda como dueno del drop
    private SellerProfile resolveOwnerForCreate() {
        if (isCurrentUserAdmin()) {
            return null;
        }
        return getCurrentSellerProfile();
    }

    private void validateSellerOwnsDrop(Drop drop) {
        if (isCurrentUserAdmin()) {
            return;
        }

        SellerProfile currentSeller = getCurrentSellerProfile();
        if (drop.getOwner() == null || !drop.getOwner().getId().equals(currentSeller.getId())) {
            throw new AccessDeniedException("You are not allowed to manage this drop");
        }
    }

    private SellerProfile getCurrentSellerProfile() {
        User currentUser = currentUserProvider.getCurrentUser();
        if (currentUser.getRole() != Role.SELLER) {
            throw new AccessDeniedException("Only sellers can manage seller-owned drops");
        }
        return sellerProfileRepository.findByUserEmail(currentUser.getEmail())
                .orElseThrow(() -> new AccessDeniedException("Seller profile not found"));
    }

    private boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
