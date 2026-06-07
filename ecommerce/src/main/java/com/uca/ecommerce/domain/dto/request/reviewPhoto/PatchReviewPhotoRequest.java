package com.uca.ecommerce.domain.dto.request.reviewPhoto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchReviewPhotoRequest {

    @Size(max = 500)
    private String url;

    @Min(value = 0)
    private Integer sortOrder;
}