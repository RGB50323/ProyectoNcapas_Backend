package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.drop.CreateDropRequest;
import com.uca.ecommerce.domain.dto.request.drop.PatchDropRequest;
import com.uca.ecommerce.domain.dto.request.drop.UpdateDropRequest;
import com.uca.ecommerce.domain.dto.response.DropResponse;

import java.util.List;
import java.util.UUID;

public interface DropService {

    List<DropResponse> getAllDrops();

    DropResponse getDropById(UUID id);

    DropResponse createDrop(CreateDropRequest request);

    DropResponse updateDrop(UpdateDropRequest request, UUID id);

    DropResponse patchDrop(PatchDropRequest request, UUID id);

    DropResponse deleteDrop(UUID id);
}
