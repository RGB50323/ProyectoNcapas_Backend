package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.request.shippingMethod.CreateShippingMethodRequest;
import com.uca.ecommerce.domain.dto.request.shippingMethod.PatchShippingMethodRequest;
import com.uca.ecommerce.domain.dto.request.shippingMethod.UpdateShippingMethodRequest;
import com.uca.ecommerce.domain.dto.response.ShippingMethodResponse;
import com.uca.ecommerce.domain.entities.ShippingMethod;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShippingMethodMapper {

    public ShippingMethod toEntityCreate(CreateShippingMethodRequest request) {
        return ShippingMethod.builder()
                .name(request.getName())
                .fee(request.getFee())
                .eta(request.getEta())
                .active(request.getActive() == null || request.getActive())
                .build();
    }

    public ShippingMethod toEntityUpdate(UpdateShippingMethodRequest request, ShippingMethod existing) {
        return ShippingMethod.builder()
                .id(existing.getId())
                .name(request.getName())
                .fee(request.getFee())
                .eta(request.getEta())
                .active(request.getActive())
                .build();
    }

    public ShippingMethod toEntityPatch(PatchShippingMethodRequest request, ShippingMethod existing) {
        return ShippingMethod.builder()
                .id(existing.getId())
                .name(request.getName() != null ? request.getName() : existing.getName())
                .fee(request.getFee() != null ? request.getFee() : existing.getFee())
                .eta(request.getEta() != null ? request.getEta() : existing.getEta())
                .active(request.getActive() != null ? request.getActive() : existing.isActive())
                .build();
    }

    public ShippingMethodResponse toDto(ShippingMethod shippingMethod) {
        return ShippingMethodResponse.builder()
                .id(shippingMethod.getId())
                .name(shippingMethod.getName())
                .fee(shippingMethod.getFee())
                .eta(shippingMethod.getEta())
                .active(shippingMethod.isActive())
                .build();
    }

    public List<ShippingMethodResponse> toDtoList(List<ShippingMethod> shippingMethods) {
        return shippingMethods.stream()
                .map(this::toDto)
                .toList();
    }
}
