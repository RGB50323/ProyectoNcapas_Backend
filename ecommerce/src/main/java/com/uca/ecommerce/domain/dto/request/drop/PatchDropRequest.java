package com.uca.ecommerce.domain.dto.request.drop;

import com.uca.ecommerce.common.Enums.DropType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchDropRequest {

    @Size(min = 1, max = 255, message = "Drop title must contain between 1 and 255 characters")
    private String title;

    @Size(min = 1, max = 255, message = "Drop slug must contain between 1 and 255 characters")
    private String slug;

    private LocalDateTime dropDate;

    @Min(value = 0, message = "Units must be greater than or equal to 0")
    private Integer units;

    private DropType type;

    @Size(max = 500, message = "Cover image URL must not exceed 500 characters")
    private String coverImageUrl;

    private Boolean removeCoverImage;

    private Boolean active;
}
