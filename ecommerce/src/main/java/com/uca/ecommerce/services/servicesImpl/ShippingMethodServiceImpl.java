package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.mappers.ShippingMethodMapper;
import com.uca.ecommerce.domain.dto.request.shippingMethod.CreateShippingMethodRequest;
import com.uca.ecommerce.domain.dto.request.shippingMethod.PatchShippingMethodRequest;
import com.uca.ecommerce.domain.dto.request.shippingMethod.UpdateShippingMethodRequest;
import com.uca.ecommerce.domain.dto.response.ShippingMethodResponse;
import com.uca.ecommerce.domain.entities.ShippingMethod;
import com.uca.ecommerce.exceptions.FieldAlreadyExistsException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.ShippingMethodRepository;
import com.uca.ecommerce.services.ShippingMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShippingMethodServiceImpl implements ShippingMethodService {

    private final ShippingMethodRepository shippingMethodRepository;
    private final ShippingMethodMapper shippingMethodMapper;

    @Override
    public List<ShippingMethodResponse> getAllShippingMethods() {
        return shippingMethodMapper.toDtoList(shippingMethodRepository.findAll());
    }

    @Override
    public ShippingMethodResponse getShippingMethodById(UUID id) {
        ShippingMethod shippingMethod = shippingMethodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Shipping method not found"));
        return shippingMethodMapper.toDto(shippingMethod);
    }

    @Override
    public ShippingMethodResponse createShippingMethod(CreateShippingMethodRequest request) {
        if (shippingMethodRepository.existsByName(request.getName())) {
            throw new FieldAlreadyExistsException("Shipping method name already exists");
        }

        ShippingMethod saved = shippingMethodRepository.save(
                shippingMethodMapper.toEntityCreate(request));
        return shippingMethodMapper.toDto(saved);
    }

    @Override
    public ShippingMethodResponse updateShippingMethod(UpdateShippingMethodRequest request, UUID id) {
        ShippingMethod existing = shippingMethodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Shipping method not found"));

        if (!existing.getName().equals(request.getName())
                && shippingMethodRepository.existsByName(request.getName())) {
            throw new FieldAlreadyExistsException("Shipping method name already exists");
        }

        ShippingMethod saved = shippingMethodRepository.save(
                shippingMethodMapper.toEntityUpdate(request, existing));
        return shippingMethodMapper.toDto(saved);
    }

    @Override
    public ShippingMethodResponse patchShippingMethod(PatchShippingMethodRequest request, UUID id) {
        ShippingMethod existing = shippingMethodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Shipping method not found"));

        if (request.getName() != null
                && !existing.getName().equals(request.getName())
                && shippingMethodRepository.existsByName(request.getName())) {
            throw new FieldAlreadyExistsException("Shipping method name already exists");
        }

        ShippingMethod saved = shippingMethodRepository.save(
                shippingMethodMapper.toEntityPatch(request, existing));
        return shippingMethodMapper.toDto(saved);
    }

    @Override
    public ShippingMethodResponse deleteShippingMethod(UUID id) {
        ShippingMethod existing = shippingMethodRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Shipping method not found"));

        shippingMethodRepository.deleteById(id);
        return shippingMethodMapper.toDto(existing);
    }
}
