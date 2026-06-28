package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.Enums.AuthStatus;
import com.uca.ecommerce.common.Enums.ProductEventType;
import com.uca.ecommerce.common.mappers.ProductMapper;
import com.uca.ecommerce.domain.dto.response.ProductResponse;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.domain.entities.UserProductEvent;
import com.uca.ecommerce.repository.ProductRepository;
import com.uca.ecommerce.repository.UserProductEventRepository;
import com.uca.ecommerce.repository.UserRepository;
import com.uca.ecommerce.services.ProductRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductRecommendationServiceImpl implements ProductRecommendationService {

    private static final int DEFAULT_LIMIT = 12;

    private final ProductRepository productRepository;
    private final UserProductEventRepository userProductEventRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponse> getRecommendationsForCurrentRequest(Integer requestedLimit) {
        int limit = resolveLimit(requestedLimit);
        boolean includeInteracted = includeInteractedProducts(requestedLimit);

        Optional<User> user = getAuthenticatedBuyer();

        if (user.isEmpty()) {
            return fallbackRecommendations(limit);
        }

        List<UserProductEvent> events =
                userProductEventRepository.findByUser_UuidOrderByCreatedAtDesc(user.get().getUuid());

        if (events.isEmpty()) {
            return fallbackRecommendations(limit);
        }

        List<Product> recommended = personalizedRecommendations(events, limit, includeInteracted);

        if (limit != Integer.MAX_VALUE && recommended.size() < limit) {
            appendFallback(recommended, limit);
        }

        return productMapper.toDtoList(recommended);
    }

    private int resolveLimit(Integer requestedLimit) {
        if (requestedLimit == null) return DEFAULT_LIMIT;
        if (requestedLimit <= 0) return Integer.MAX_VALUE;
        return requestedLimit;
    }

    private boolean includeInteractedProducts(Integer requestedLimit) {
        return requestedLimit != null && requestedLimit <= 0;
    }

    private Optional<User> getAuthenticatedBuyer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            return Optional.empty();
        }

        boolean isBuyer = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_BUYER"));

        if (!isBuyer) {
            return Optional.empty();
        }

        return userRepository.findByEmail(authentication.getName());
    }

    private List<Product> personalizedRecommendations(List<UserProductEvent> events, int limit, boolean includeInteracted) {
        Map<UUID, Integer> categoryScores = new HashMap<>();
        Map<UUID, Integer> brandScores = new HashMap<>();
        Set<UUID> interactedProductIds = events.stream()
                .map(event -> event.getProduct().getId())
                .collect(Collectors.toSet());

        for (UserProductEvent event : events) {
            Product product = event.getProduct();
            int weight = weightFor(event.getType());

            categoryScores.merge(product.getCategory().getId(), weight, Integer::sum);
            brandScores.merge(product.getBrand().getId(), weight, Integer::sum);
        }

        List<Product> candidates = productRepository.findByAuthStatusAndTotalStockGreaterThanOrderByCreatedAtDesc(AuthStatus.AUTHENTICATED, 0);

        return candidates.stream()
                .filter(product -> includeInteracted || !interactedProductIds.contains(product.getId()))
                .map(product -> Map.entry(product, scoreProduct(product, categoryScores, brandScores)))
                .filter(entry -> includeInteracted || entry.getValue() > 0)
                .sorted(
                        Map.Entry.<Product, Integer>comparingByValue().reversed()
                                .thenComparing(entry -> entry.getKey().getCreatedAt(), Comparator.reverseOrder())
                )
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private int scoreProduct(
            Product product,
            Map<UUID, Integer> categoryScores,
            Map<UUID, Integer> brandScores
    ) {
        int score = 0;
        score += categoryScores.getOrDefault(product.getCategory().getId(), 0);
        score += brandScores.getOrDefault(product.getBrand().getId(), 0);
        return score;
    }

    private int weightFor(ProductEventType type) {
        return switch (type) {
            case VIEW -> 1;
            case ADD_TO_CART -> 3;
            case PURCHASE -> 5;
        };
    }

    private List<ProductResponse> fallbackRecommendations(int limit) {
        List<Product> fallback = productRepository.findByAuthStatusAndTotalStockGreaterThanOrderByCreatedAtDesc(AuthStatus.AUTHENTICATED, 0);

        return productMapper.toDtoList(
                fallback.stream()
                        .limit(limit)
                        .collect(Collectors.toList())
        );
    }

    private void appendFallback(List<Product> recommended, int limit) {
        Map<UUID, Product> byId = new LinkedHashMap<>();

        for (Product product : recommended) {
            byId.put(product.getId(), product);
        }

        List<Product> fallback = productRepository.findByAuthStatusAndTotalStockGreaterThanOrderByCreatedAtDesc(AuthStatus.AUTHENTICATED, 0);

        for (Product product : fallback) {
            if (byId.size() >= limit) {
                break;
            }

            byId.putIfAbsent(product.getId(), product);
        }

        recommended.clear();
        recommended.addAll(new ArrayList<>(byId.values()));
    }
}
