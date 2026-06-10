package com.uca.ecommerce.domain.dto.response;

import com.uca.ecommerce.common.Enums.DropType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DropResponse {
    private UUID id;
    private String title;
    private String slug;
    private LocalDateTime dropDate;
    private Integer units;
    private DropType type;
    private String coverImageUrl;
    private Boolean active;
    private LocalDateTime createdAt;
}
