package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.storeRequest.CreateStoreRequestRequest;
import com.uca.ecommerce.domain.dto.request.storeRequest.ReviewStoreRequestRequest;
import com.uca.ecommerce.domain.dto.response.StoreRequestResponse;

import java.util.List;
import java.util.UUID;

public interface StoreRequestService {
    StoreRequestResponse create(CreateStoreRequestRequest request);
    StoreRequestResponse getMyLatest();
    List<StoreRequestResponse> getAll();
    List<StoreRequestResponse> getPending();
    StoreRequestResponse review(UUID id, ReviewStoreRequestRequest request);
}
