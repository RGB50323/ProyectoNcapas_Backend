package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.shippingMethod.CreateShippingMethodRequest;
import com.uca.ecommerce.domain.dto.request.shippingMethod.PatchShippingMethodRequest;
import com.uca.ecommerce.domain.dto.request.shippingMethod.UpdateShippingMethodRequest;
import com.uca.ecommerce.domain.dto.response.ShippingMethodResponse;

import java.util.List;
import java.util.UUID;

public interface ShippingMethodService {

    List<ShippingMethodResponse> getAllShippingMethods();

    ShippingMethodResponse getShippingMethodById(UUID id);

    ShippingMethodResponse createShippingMethod(CreateShippingMethodRequest request);

    ShippingMethodResponse updateShippingMethod(UpdateShippingMethodRequest request, UUID id);

    ShippingMethodResponse patchShippingMethod(PatchShippingMethodRequest request, UUID id);

    ShippingMethodResponse deleteShippingMethod(UUID id);
}
