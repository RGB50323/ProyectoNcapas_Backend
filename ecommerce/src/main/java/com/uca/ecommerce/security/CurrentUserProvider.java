package com.uca.ecommerce.security;

import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final UserRepository userRepository;

    // JwtAuthFilter stores the user email as the authentication principal/name
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new NotFoundException("Authenticated user not found");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new NotFoundException("Authenticated user not found"));
    }
}
