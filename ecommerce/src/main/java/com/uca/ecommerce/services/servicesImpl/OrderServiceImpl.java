package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.Enums.DiscountType;
import com.uca.ecommerce.common.Enums.OrderStatus;
import com.uca.ecommerce.common.mappers.OrderMapper;
import com.uca.ecommerce.domain.dto.request.order.CreateOrderRequest;
import com.uca.ecommerce.domain.dto.request.order.PatchOrderRequest;
import com.uca.ecommerce.domain.dto.request.order.UpdateOrderRequest;
import com.uca.ecommerce.domain.dto.response.OrderResponse;
import com.uca.ecommerce.domain.entities.*;
import com.uca.ecommerce.exceptions.InvalidOrderStatusTransitionException;
import com.uca.ecommerce.exceptions.InvalidProductPatchException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.*;
import com.uca.ecommerce.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final CouponRepository couponRepository;
    private final OrderMapper orderMapper;

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderMapper.toDtoList(orderRepository.findAll());
    }

    @Override
    public List<OrderResponse> getOrdersByCustomerId(UUID customerId) {
        userRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return orderMapper.toDtoList(orderRepository.findByCustomerUuid(customerId));
    }

    @Override
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderMapper.toDtoList(orderRepository.findByStatus(status));
    }

    @Override
    public OrderResponse getOrderById(UUID id) {
        return orderMapper.toDto(findOrThrow(id));
    }

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Address shippingAddress = addressRepository.findById(request.getShippingAddressId())
                .orElseThrow(() -> new NotFoundException("Shipping address not found"));

        ShippingMethod shippingMethod = shippingMethodRepository.findById(request.getShippingMethodId())
                .orElseThrow(() -> new NotFoundException("Shipping method not found"));

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;
        Coupon coupon = null;

        if (request.getCouponId() != null) {
            coupon = couponRepository.findById(request.getCouponId())
                    .orElseThrow(() -> new NotFoundException("Coupon not found"));

            if (!coupon.isActive())
                throw new RuntimeException("Coupon is not active");

            if (coupon.getExpiresAt() != null
                    && coupon.getExpiresAt().isBefore(LocalDateTime.now()))
                throw new RuntimeException("Coupon has expired");

            if (coupon.getMaxUses() != null
                    && coupon.getUsesCount() >= coupon.getMaxUses())
                throw new RuntimeException("Coupon has reached its maximum uses");

            discountAmount = calculateDiscount(subtotal, shippingMethod.getFee(), coupon);
        }

        BigDecimal total = subtotal
                .add(shippingMethod.getFee())
                .subtract(discountAmount);

        return orderMapper.toDto(
                orderRepository.save(
                        orderMapper.toEntityCreate(
                                request, customer, shippingAddress,
                                shippingMethod, coupon,
                                subtotal, discountAmount, total
                        )
                )
        );
    }

    @Override
    public OrderResponse updateOrder(UpdateOrderRequest request, UUID id) {
        Order existing = findOrThrow(id);

        Address shippingAddress = addressRepository.findById(request.getShippingAddressId())
                .orElseThrow(() -> new NotFoundException("Shipping address not found"));

        ShippingMethod shippingMethod = shippingMethodRepository.findById(request.getShippingMethodId())
                .orElseThrow(() -> new NotFoundException("Shipping method not found"));

        Coupon coupon = null;
        if (request.getCouponId() != null) {
            coupon = couponRepository.findById(request.getCouponId())
                    .orElseThrow(() -> new NotFoundException("Coupon not found"));
        }

        return orderMapper.toDto(
                orderRepository.save(
                        orderMapper.toEntityUpdate(
                                request, existing, shippingAddress, shippingMethod, coupon
                        )
                )
        );
    }

    @Override
    public OrderResponse deleteOrder(UUID id) {
        Order existing = findOrThrow(id);
        orderRepository.deleteById(id);
        return orderMapper.toDto(existing);
    }

    private BigDecimal calculateDiscount(
            BigDecimal subtotal, BigDecimal shippingFee, Coupon coupon) {

        return switch (coupon.getType()) {
            case PERCENTAGE -> subtotal
                    .multiply(coupon.getValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            case FIXED -> coupon.getValue().min(subtotal);
            case FREE_SHIPPING -> shippingFee;
            case TWO_FOR_ONE -> BigDecimal.ZERO;
        };
    }

    private Order findOrThrow(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));
    }

    @Override
    public OrderResponse patchOrder(PatchOrderRequest request, UUID id) {
        Order existing = findOrThrow(id);

        if (existing.getStatus() == OrderStatus.DELIVERED
                && request.getStatus() != null
                && request.getStatus() != OrderStatus.DELIVERED
                && request.getStatus() != OrderStatus.REFUNDED) {
            throw new InvalidOrderStatusTransitionException(
                    "A delivered order can only be moved to REFUNDED");
        }

        if (Boolean.TRUE.equals(request.getRemoveCoupon())
                && request.getCouponId() != null)
            throw new InvalidProductPatchException(
                    "Coupon ID cannot be provided when removeCoupon is true"
            );

        if (Boolean.TRUE.equals(request.getRemoveTrackingNumber())
                && request.getTrackingNumber() != null)
            throw new InvalidProductPatchException(
                    "Tracking number cannot be provided when removeTrackingNumber is true"
            );

        if (Boolean.TRUE.equals(request.getRemoveNotes())
                && request.getNotes() != null)
            throw new InvalidProductPatchException(
                    "Notes cannot be provided when removeNotes is true"
            );

        Address shippingAddress = request.getShippingAddressId() == null
                ? null
                : addressRepository.findById(request.getShippingAddressId())
                  .orElseThrow(() -> new NotFoundException("Shipping address not found"));

        ShippingMethod shippingMethod = request.getShippingMethodId() == null
                ? null
                : shippingMethodRepository.findById(request.getShippingMethodId())
                  .orElseThrow(() -> new NotFoundException("Shipping method not found"));

        Coupon coupon = request.getCouponId() == null
                ? null
                : couponRepository.findById(request.getCouponId())
                  .orElseThrow(() -> new NotFoundException("Coupon not found"));

        return orderMapper.toDto(
                orderRepository.save(
                        orderMapper.toEntityPatch(
                                request, existing, shippingAddress, shippingMethod, coupon
                        )
                )
        );
    }
}