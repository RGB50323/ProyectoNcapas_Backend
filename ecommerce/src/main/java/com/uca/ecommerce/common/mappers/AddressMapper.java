package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.request.address.CreateAddressRequest;
import com.uca.ecommerce.domain.dto.request.address.UpdateAddressRequest;
import com.uca.ecommerce.domain.dto.response.AddressResponse;
import com.uca.ecommerce.domain.dto.response.UserResponse;
import com.uca.ecommerce.domain.entities.Address;
import com.uca.ecommerce.domain.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AddressMapper {

    public Address toEntityCreate(CreateAddressRequest request, User user) {
        return Address.builder()
                .user(user)
                .alias(request.getAlias())
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .isDefault(request.isDefault())
                .build();
    }

    public Address toEntityUpdate(UpdateAddressRequest request, Address existing) {
        return Address.builder()
                .id(existing.getId())
                .user(existing.getUser())
                .alias(request.getAlias())
                .street(request.getStreet())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .isDefault(request.isDefault())
                .build();
    }

    public AddressResponse toDto(Address address) {
        User user = address.getUser();
        return AddressResponse.builder()
                .id(address.getId())
                .alias(address.getAlias())
                .street(address.getStreet())
                .city(address.getCity())
                .state(address.getState())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .isDefault(address.isDefault())
                .user(UserResponse.builder()
                        .uuid(user.getUuid())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .role(user.getRole().name())
                        .build())
                .build();
    }

    public List<AddressResponse> toDtoList(List<Address> addresses) {
        return addresses.stream().map(this::toDto).toList();
    }
}