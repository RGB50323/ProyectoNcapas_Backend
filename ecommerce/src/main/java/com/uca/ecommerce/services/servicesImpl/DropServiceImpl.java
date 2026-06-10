package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.mappers.DropMapper;
import com.uca.ecommerce.domain.dto.request.drop.CreateDropRequest;
import com.uca.ecommerce.domain.dto.request.drop.PatchDropRequest;
import com.uca.ecommerce.domain.dto.request.drop.UpdateDropRequest;
import com.uca.ecommerce.domain.dto.response.DropResponse;
import com.uca.ecommerce.domain.entities.Drop;
import com.uca.ecommerce.exceptions.FieldAlreadyExistsException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.DropRepository;
import com.uca.ecommerce.services.DropService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DropServiceImpl implements DropService {

    private final DropRepository dropRepository;
    private final DropMapper dropMapper;

    @Override
    public List<DropResponse> getAllDrops() {
        return dropMapper.toDtoList(dropRepository.findAll());
    }

    @Override
    public DropResponse getDropById(UUID id) {
        Drop drop = dropRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Drop not found"));
        return dropMapper.toDto(drop);
    }

    @Override
    public DropResponse createDrop(CreateDropRequest request) {
        if (dropRepository.existsBySlug(request.getSlug())) {
            throw new FieldAlreadyExistsException("Drop slug already exists");
        }

        Drop saved = dropRepository.save(dropMapper.toEntityCreate(request));
        return dropMapper.toDto(saved);
    }

    @Override
    public DropResponse updateDrop(UpdateDropRequest request, UUID id) {
        Drop existing = dropRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Drop not found"));

        if (!existing.getSlug().equals(request.getSlug())
                && dropRepository.existsBySlug(request.getSlug())) {
            throw new FieldAlreadyExistsException("Drop slug already exists");
        }

        Drop saved = dropRepository.save(dropMapper.toEntityUpdate(request, existing));
        return dropMapper.toDto(saved);
    }

    @Override
    public DropResponse patchDrop(PatchDropRequest request, UUID id) {
        Drop existing = dropRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Drop not found"));

        if (request.getSlug() != null
                && !existing.getSlug().equals(request.getSlug())
                && dropRepository.existsBySlug(request.getSlug())) {
            throw new FieldAlreadyExistsException("Drop slug already exists");
        }

        Drop saved = dropRepository.save(dropMapper.toEntityPatch(request, existing));
        return dropMapper.toDto(saved);
    }

    @Override
    public DropResponse deleteDrop(UUID id) {
        Drop existing = dropRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Drop not found"));

        dropRepository.deleteById(id);
        return dropMapper.toDto(existing);
    }
}
