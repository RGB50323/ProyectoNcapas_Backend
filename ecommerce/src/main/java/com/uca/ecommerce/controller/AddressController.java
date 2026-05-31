package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.request.address.CreateAddressRequest;
import com.uca.ecommerce.domain.dto.request.address.UpdateAddressRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController extends BaseController {

    private final AddressService addressService;

    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllAddresses() {
        return buildResponse("Addresses retrieved successfully", HttpStatus.OK, addressService.getAllAddresses());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<GeneralResponse> getAddressesByUserId(@PathVariable UUID userId) {
        return buildResponse("Addresses retrieved successfully", HttpStatus.OK, addressService.getAddressesByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getAddressById(@PathVariable UUID id) {
        return buildResponse("Address retrieved successfully", HttpStatus.OK, addressService.getAddressById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createAddress(@Valid @RequestBody CreateAddressRequest request) {
        return buildResponse("Address created successfully", HttpStatus.CREATED, addressService.createAddress(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GeneralResponse> updateAddress(@Valid @RequestBody UpdateAddressRequest request, @PathVariable UUID id) {
        return buildResponse("Address updated successfully", HttpStatus.OK, addressService.updateAddress(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteAddress(@PathVariable UUID id) {
        return buildResponse("Address deleted successfully", HttpStatus.OK, addressService.deleteAddress(id));
    }
}