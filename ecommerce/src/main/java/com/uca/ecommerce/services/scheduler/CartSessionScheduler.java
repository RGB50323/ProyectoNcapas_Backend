package com.uca.ecommerce.services.scheduler;

import com.uca.ecommerce.common.Enums.CartSessionStatus;
import com.uca.ecommerce.domain.entities.CartItem;
import com.uca.ecommerce.domain.entities.CartSession;
import com.uca.ecommerce.repository.CartItemRepository;
import com.uca.ecommerce.repository.CartSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CartSessionScheduler {

    private final CartSessionRepository cartSessionRepository;
    private final CartItemRepository cartItemRepository;

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void markAbandonedSessions() {
        log.info("Corriendo job de abandono de carrito...");

        List<CartSession> activeSessions = cartSessionRepository.findByStatus(CartSessionStatus.ACTIVE);
        LocalDateTime threshold = LocalDateTime.now().minusHours(72);

        for (CartSession session : activeSessions) {
            List<CartItem> items = cartItemRepository.findByUser_Uuid(session.getUser().getUuid());

            boolean abandoned = items.isEmpty() ||
                    items.stream().allMatch(item -> item.getAddedAt().isBefore(threshold));

            if (abandoned) {
                session.setStatus(CartSessionStatus.ABANDONED);
                session.setAbandonedAt(LocalDateTime.now());
                session.setAbandonedManually(false);
                cartSessionRepository.save(session);
                log.info("Sesión {} marcada como abandonada por inactividad", session.getId());
            }
        }

        log.info("Job completado.");
    }
}