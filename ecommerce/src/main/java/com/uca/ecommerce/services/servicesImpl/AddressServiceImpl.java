package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.Enums.Role;
import com.uca.ecommerce.common.mappers.AddressMapper;
import com.uca.ecommerce.domain.dto.request.address.CreateAddressRequest;
import com.uca.ecommerce.domain.dto.request.address.UpdateAddressRequest;
import com.uca.ecommerce.domain.dto.response.AddressResponse;
import com.uca.ecommerce.domain.entities.Address;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.AddressRepository;
import com.uca.ecommerce.repository.UserRepository;
import com.uca.ecommerce.security.CurrentUserProvider;
import com.uca.ecommerce.services.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public List<AddressResponse> getAllAddresses() {
        return addressMapper.toDtoList(addressRepository.findAll());
    }

    @Override
    public List<AddressResponse> getAddressesByUserId(UUID userId) {
        User currentUser = currentUserProvider.getCurrentUser();
        if (!currentUser.getUuid().equals(userId) && currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("No tienes permiso para ver estas direcciones");
        }
        return addressMapper.toDtoList(addressRepository.findByUserUuid(userId));
    }

    @Override
    public AddressResponse getAddressById(UUID id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Address not found"));
        User currentUser = currentUserProvider.getCurrentUser();
        if (!currentUser.getUuid().equals(address.getUser().getUuid()) && currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("No tienes permiso para ver esta dirección");
        }
        return addressMapper.toDto(address);
    }

    @Override
    @Transactional
    public AddressResponse createAddress(CreateAddressRequest request) {
        User currentUser = currentUserProvider.getCurrentUser();

        if (request.isDefault() && addressRepository.existsByUserUuidAndIsDefaultTrue(currentUser.getUuid())) {
            addressRepository.findByUserUuidAndIsDefaultTrue(currentUser.getUuid())
                    .ifPresent(existing -> {
                        existing.setDefault(false);
                        addressRepository.save(existing);
                    });
        }

        return addressMapper.toDto(
                addressRepository.save(addressMapper.toEntityCreate(request, currentUser))
        );
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(UpdateAddressRequest request, UUID id) {
        Address existing = addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Address not found"));
        User currentUser = currentUserProvider.getCurrentUser();
        if (!currentUser.getUuid().equals(existing.getUser().getUuid()) && currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("No tienes permiso para modificar esta dirección");
        }

        if (request.isDefault() && !existing.isDefault()) {
            addressRepository.findByUserUuidAndIsDefaultTrue(existing.getUser().getUuid())
                    .ifPresent(prev -> {
                        prev.setDefault(false);
                        addressRepository.save(prev);
                    });
        }

        return addressMapper.toDto(
                addressRepository.save(addressMapper.toEntityUpdate(request, existing))
        );
    }

    @Override
    public AddressResponse deleteAddress(UUID id) {
        AddressResponse existing = this.getAddressById(id);
        addressRepository.deleteById(id);
        return existing;
    }

    @Override
    @Transactional
    public void deleteAddressesByUserId(UUID userId) {
        addressRepository.deleteAllByUserUuid(userId);
    }
}