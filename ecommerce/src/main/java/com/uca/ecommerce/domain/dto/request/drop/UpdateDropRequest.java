package com.uca.ecommerce.domain.dto.request.drop;

import com.uca.ecommerce.common.Enums.DropType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDropRequest {

    @NotBlank(message = "Drop title is required")
    @Size(max = 255, message = "Drop title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Drop slug is required")
    @Size(max = 255, message = "Drop slug must not exceed 255 characters")
    private String slug;

    @NotNull(message = "Drop date is required")
    private LocalDateTime dropDate;

    @NotNull(message = "Drop type is required")
    private DropType type;

    @Size(max = 500, message = "Cover image URL must not exceed 500 characters")
    private String coverImageUrl;

    @NotNull(message = "Active flag is required")
    private Boolean active;
}
