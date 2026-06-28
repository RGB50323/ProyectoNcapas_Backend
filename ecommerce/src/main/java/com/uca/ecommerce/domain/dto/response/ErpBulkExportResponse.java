package com.uca.ecommerce.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErpBulkExportResponse {
    private int exportedCount;
    private int rejectedCount;
    private int failedCount;
    private int skippedCount;
    private int processedCount;
    private List<ErpExportResponse> results;
}
