package com.uca.ecommerce.services.discount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

// Carries everything a discount strategy may need, decoupled from the Order/Cart entities
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiscountContext {

    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private List<LineItem> items;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LineItem {
        private UUID productId;
        private BigDecimal unitPrice;
        private Integer quantity;
    }
}
