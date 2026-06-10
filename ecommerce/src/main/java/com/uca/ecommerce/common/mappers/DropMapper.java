package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.request.drop.CreateDropRequest;
import com.uca.ecommerce.domain.dto.request.drop.PatchDropRequest;
import com.uca.ecommerce.domain.dto.request.drop.UpdateDropRequest;
import com.uca.ecommerce.domain.dto.response.DropResponse;
import com.uca.ecommerce.domain.entities.Drop;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DropMapper {

    public Drop toEntityCreate(CreateDropRequest request) {
        return Drop.builder()
                .title(request.getTitle())
                .slug(request.getSlug())
                .dropDate(request.getDropDate())
                .units(request.getUnits())
                .type(request.getType())
                .coverImageUrl(request.getCoverImageUrl())
                .active(request.getActive() == null || request.getActive())
                .build();
    }

    public Drop toEntityUpdate(UpdateDropRequest request, Drop existing) {
        return Drop.builder()
                .id(existing.getId())
                .title(request.getTitle())
                .slug(request.getSlug())
                .dropDate(request.getDropDate())
                .units(request.getUnits())
                .type(request.getType())
                .coverImageUrl(request.getCoverImageUrl())
                .active(request.getActive())
                .createdAt(existing.getCreatedAt())
                .build();
    }

    public Drop toEntityPatch(PatchDropRequest request, Drop existing) {
        return Drop.builder()
                .id(existing.getId())
                .title(request.getTitle() != null ? request.getTitle() : existing.getTitle())
                .slug(request.getSlug() != null ? request.getSlug() : existing.getSlug())
                .dropDate(request.getDropDate() != null ? request.getDropDate() : existing.getDropDate())
                .units(request.getUnits() != null ? request.getUnits() : existing.getUnits())
                .type(request.getType() != null ? request.getType() : existing.getType())
                .coverImageUrl(Boolean.TRUE.equals(request.getRemoveCoverImage())
                        ? null
                        : request.getCoverImageUrl() != null
                          ? request.getCoverImageUrl()
                          : existing.getCoverImageUrl())
                .active(request.getActive() != null ? request.getActive() : existing.isActive())
                .createdAt(existing.getCreatedAt())
                .build();
    }

    public DropResponse toDto(Drop drop) {
        return DropResponse.builder()
                .id(drop.getId())
                .title(drop.getTitle())
                .slug(drop.getSlug())
                .dropDate(drop.getDropDate())
                .units(drop.getUnits())
                .type(drop.getType())
                .coverImageUrl(drop.getCoverImageUrl())
                .active(drop.isActive())
                .createdAt(drop.getCreatedAt())
                .build();
    }

    public List<DropResponse> toDtoList(List<Drop> drops) {
        return drops.stream()
                .map(this::toDto)
                .toList();
    }
}
