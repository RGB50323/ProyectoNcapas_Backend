package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.mappers.AddressMapper;
import com.uca.ecommerce.domain.dto.request.address.CreateAddressRequest;
import com.uca.ecommerce.domain.dto.request.address.UpdateAddressRequest;
import com.uca.ecommerce.domain.dto.response.AddressResponse;
import com.uca.ecommerce.domain.entities.Address;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.AddressRepository;
import com.uca.ecommerce.repository.UserRepository;
import com.uca.ecommerce.services.AddressService;
import lombok.RequiredArgsConstructor;
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

    @Override
    public List<AddressResponse> getAllAddresses() {
        return addressMapper.toDtoList(addressRepository.findAll());
    }

    @Override
    public List<AddressResponse> getAddressesByUserId(UUID userId) {
        return addressMapper.toDtoList(addressRepository.findByUserUuid(userId));
    }

    @Override
    public AddressResponse getAddressById(UUID id) {
        return addressMapper.toDto(addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Address not found")));
    }

    @Override
    @Transactional
    public AddressResponse createAddress(CreateAddressRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (request.isDefault() && addressRepository.existsByUserUuidAndIsDefaultTrue(request.getUserId())) {
            addressRepository.findByUserUuidAndIsDefaultTrue(request.getUserId())
                    .ifPresent(existing -> {
                        existing.setDefault(false);
                        addressRepository.save(existing);
                    });
        }

        return addressMapper.toDto(
                addressRepository.save(addressMapper.toEntityCreate(request, user))
        );
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(UpdateAddressRequest request, UUID id) {
        Address existing = addressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Address not found"));

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
}