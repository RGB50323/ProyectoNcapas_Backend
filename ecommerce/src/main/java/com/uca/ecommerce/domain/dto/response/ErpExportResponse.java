package com.uca.ecommerce.domain.dto.response;

import com.uca.ecommerce.common.Enums.ErpExportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErpExportResponse {
    private UUID orderId;
    private ErpExportStatus erpExportStatus;
    private String erpReference;
    private LocalDateTime erpExportedAt;
    private String erpErrorMessage;
    private ErpOrderResponse erpOrder;
}
