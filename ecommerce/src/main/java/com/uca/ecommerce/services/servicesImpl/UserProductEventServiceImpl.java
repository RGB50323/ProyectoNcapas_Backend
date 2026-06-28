package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.Enums.ProductEventType;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.domain.entities.UserProductEvent;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.ProductRepository;
import com.uca.ecommerce.repository.UserProductEventRepository;
import com.uca.ecommerce.security.CurrentUserProvider;
import com.uca.ecommerce.services.UserProductEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProductEventServiceImpl implements UserProductEventService {

    private static final long VIEW_DEDUPLICATION_MINUTES = 30;

    private final UserProductEventRepository userProductEventRepository;
    private final ProductRepository productRepository;
    private final CurrentUserProvider currentUserProvider;

    @Override
    public void registerCurrentBuyerEvent(UUID productId, ProductEventType type) {
        User user = currentUserProvider.getCurrentUser();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (type == ProductEventType.VIEW && wasRecentlyViewed(user, product)) {
            return;
        }

        userProductEventRepository.save(
                UserProductEvent.builder()
                        .user(user)
                        .product(product)
                        .type(type)
                        .build()
        );
    }

    private boolean wasRecentlyViewed(User user, Product product) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(VIEW_DEDUPLICATION_MINUTES);

        return userProductEventRepository.existsByUser_UuidAndProduct_IdAndTypeAndCreatedAtAfter(
                user.getUuid(),
                product.getId(),
                ProductEventType.VIEW,
                threshold
        );
    }
}
