package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.address.CreateAddressRequest;
import com.uca.ecommerce.domain.dto.request.address.UpdateAddressRequest;
import com.uca.ecommerce.domain.dto.response.AddressResponse;

import java.util.List;
import java.util.UUID;

public interface AddressService {
    List<AddressResponse> getAllAddresses();
    List<AddressResponse> getAddressesByUserId(UUID userId);
    AddressResponse getAddressById(UUID id);
    AddressResponse createAddress(CreateAddressRequest request);
    AddressResponse updateAddress(UpdateAddressRequest request, UUID id);
    AddressResponse deleteAddress(UUID id);
    void deleteAddressesByUserId(UUID userId);
}