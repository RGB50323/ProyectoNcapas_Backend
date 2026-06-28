package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.mappers.OrderItemMapper;
import com.uca.ecommerce.domain.dto.request.orderItem.CreateOrderItemRequest;
import com.uca.ecommerce.domain.dto.request.orderItem.PatchOrderItemRequest;
import com.uca.ecommerce.domain.dto.request.orderItem.UpdateOrderItemRequest;
import com.uca.ecommerce.domain.dto.response.OrderItemResponse;
import com.uca.ecommerce.domain.entities.*;
import com.uca.ecommerce.exceptions.FieldAlreadyExistsException;
import com.uca.ecommerce.exceptions.InvalidProductPatchException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.*;
import com.uca.ecommerce.services.OrderItemService;
import com.uca.ecommerce.services.discount.DiscountContext;
import com.uca.ecommerce.services.discount.DiscountStrategyResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final OrderItemMapper orderItemMapper;
    private final DiscountStrategyResolver discountStrategyResolver;

    @Override
    public List<OrderItemResponse> getAllOrderItems() {
        return orderItemMapper.toDtoList(orderItemRepository.findAll());
    }

    @Override
    public List<OrderItemResponse> getItemsByOrderId(UUID orderId) {
        orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        return orderItemMapper.toDtoList(orderItemRepository.findByOrderId(orderId));
    }

    @Override
    public List<OrderItemResponse> getItemsByProductId(UUID productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        return orderItemMapper.toDtoList(orderItemRepository.findByProductId(productId));
    }

    @Override
    public List<OrderItemResponse> getItemsBySellerId(UUID sellerId) {
        sellerProfileRepository.findById(sellerId)
                .orElseThrow(() -> new NotFoundException("Seller not found"));
        return orderItemMapper.toDtoList(orderItemRepository.findBySellerId(sellerId));
    }

    @Override
    public OrderItemResponse getOrderItemById(UUID id) {
        return orderItemMapper.toDto(findOrThrow(id));
    }

    @Override
    @Transactional
    public OrderItemResponse createOrderItem(CreateOrderItemRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        SellerProfile seller = sellerProfileRepository.findById(request.getSellerId())
                .orElseThrow(() -> new NotFoundException("Seller not found"));

        ProductVariant variant = null;
        if (request.getVariantId() != null) {
            variant = productVariantRepository.findById(request.getVariantId())
                    .orElseThrow(() -> new NotFoundException("Product variant not found"));
        }

        // No se permite duplicar el mismo producto+variante en la misma orden
        UUID variantId = variant != null ? variant.getId() : null;
        if (orderItemRepository.existsByOrderIdAndProductIdAndVariantId(
                request.getOrderId(), request.getProductId(), variantId))
            throw new FieldAlreadyExistsException(
                    "This product with the selected variant is already in the order"
            );

        // ─── Validar y descontar stock ────────────────────────────
        if (variant != null) {
            // Con variante: validar y descontar stock de la variante
            if (variant.getStock() < request.getQuantity())
                throw new RuntimeException(
                        "Stock insuficiente para la variante seleccionada. Disponible: "
                                + variant.getStock()
                );
            variant.setStock(variant.getStock() - request.getQuantity());
            productVariantRepository.save(variant);
        } else {
            // Sin variante: validar stock total del producto
            if (product.getTotalStock() < request.getQuantity())
                throw new RuntimeException(
                        "Stock insuficiente para el producto. Disponible: "
                                + product.getTotalStock()
                );
        }

        // Recalcular totalStock del producto usando la query del repositorio
        Long newTotalStock = productVariantRepository.sumStockByProductId(product.getId());
        product.setTotalStock(newTotalStock != null ? newTotalStock.intValue() : 0);
        productRepository.save(product);
        // ──────────────────────────────────────────────────────────

        OrderItem saved = orderItemRepository.save(
                orderItemMapper.toEntityCreate(request, order, product, variant, seller)
        );

        // Actualizar subtotal y total de la orden
        updateOrderTotals(order);

        return orderItemMapper.toDto(saved);
    }

    @Override
    @Transactional
    public OrderItemResponse updateOrderItem(UpdateOrderItemRequest request, UUID id) {
        OrderItem existing = findOrThrow(id);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        SellerProfile seller = sellerProfileRepository.findById(request.getSellerId())
                .orElseThrow(() -> new NotFoundException("Seller not found"));

        ProductVariant variant = null;
        if (request.getVariantId() != null) {
            variant = productVariantRepository.findById(request.getVariantId())
                    .orElseThrow(() -> new NotFoundException("Product variant not found"));
        }

        OrderItem saved = orderItemRepository.save(
                orderItemMapper.toEntityUpdate(request, existing, product, variant, seller)
        );

        updateOrderTotals(existing.getOrder());

        return orderItemMapper.toDto(saved);
    }

    @Override
    @Transactional
    public OrderItemResponse patchOrderItem(PatchOrderItemRequest request, UUID id) {
        OrderItem existing = findOrThrow(id);

        if (Boolean.TRUE.equals(request.getRemoveVariant())
                && request.getVariantId() != null)
            throw new InvalidProductPatchException(
                    "Variant ID cannot be provided when removeVariant is true"
            );

        ProductVariant variant = null;
        if (request.getVariantId() != null) {
            variant = productVariantRepository.findById(request.getVariantId())
                    .orElseThrow(() -> new NotFoundException("Product variant not found"));
        }

        OrderItem saved = orderItemRepository.save(
                orderItemMapper.toEntityPatch(request, existing, variant)
        );

        updateOrderTotals(existing.getOrder());

        return orderItemMapper.toDto(saved);
    }

    @Override
    @Transactional
    public OrderItemResponse deleteOrderItem(UUID id) {
        OrderItem existing = findOrThrow(id);
        orderItemRepository.deleteById(id);

        // Recalcular totales después de eliminar el item
        updateOrderTotals(existing.getOrder());

        return orderItemMapper.toDto(existing);
    }

    // Recalcula subtotal, descuento y total de la orden cada vez que cambia un item
    private void updateOrderTotals(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());

        BigDecimal subtotal = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = calculateDiscount(order, items, subtotal);

        BigDecimal total = subtotal
                .add(order.getShippingCost())
                .subtract(discount)
                .max(BigDecimal.ZERO);

        order.setSubtotal(subtotal);
        order.setDiscountAmount(discount);
        order.setTotal(total);
        orderRepository.save(order);
    }

    // El descuento del cupón se calcula sobre el subtotal real, ya con los items agregados
    private BigDecimal calculateDiscount(Order order, List<OrderItem> items, BigDecimal subtotal) {
        Coupon coupon = order.getCoupon();
        if (coupon == null) {
            return BigDecimal.ZERO;
        }

        List<DiscountContext.LineItem> lineItems = items.stream()
                .map(item -> DiscountContext.LineItem.builder()
                        .productId(item.getProduct().getId())
                        .unitPrice(item.getUnitPrice())
                        .quantity(item.getQuantity())
                        .build())
                .toList();

        DiscountContext context = DiscountContext.builder()
                .subtotal(subtotal)
                .shippingCost(order.getShippingCost())
                .items(lineItems)
                .build();

        return discountStrategyResolver.resolve(coupon.getType())
                .calculate(context, coupon)
                .min(subtotal.add(order.getShippingCost()))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private OrderItem findOrThrow(UUID id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order item not found"));
    }
}