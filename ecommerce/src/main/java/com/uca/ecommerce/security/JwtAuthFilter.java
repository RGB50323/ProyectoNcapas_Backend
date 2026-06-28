package com.uca.ecommerce.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uca.ecommerce.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        if (path.startsWith("/auth/")) {
            return true;
        }

        if ("GET".equals(method)) {
            if (path.equals("/products/recommended")) {
                return false;
            }
            if (path.equals("/drops/my")) {
                return false;
            }
            if (path.startsWith("/reviews/reviewable-products/")
                    || path.startsWith("/reviews/seller/")) {
                return false;
            }
            return path.equals("/uploads") || path.startsWith("/uploads/")
                    || path.equals("/products/public")
                    || path.equals("/categories") || path.startsWith("/categories/")
                    || path.equals("/brands") || path.startsWith("/brands/")
                    || path.equals("/product-variants/public")
                    || path.equals("/product-images/public")
                    || path.equals("/product-badges/public")
                    || path.equals("/shipping-methods") || path.startsWith("/shipping-methods/")
                    || path.equals("/drops") || path.startsWith("/drops/")
                    || path.equals("/drop-products") || path.startsWith("/drop-products/")
                    || path.equals("/reviews") || path.startsWith("/reviews/")
                    || path.equals("/review-photos") || path.startsWith("/review-photos/");
        }

        return false;
    }

    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtService.validateToken(token)) {
            sendUnauthorized(response, "Invalid or expired token");
            return;
        }

        String email = jwtService.extractEmail(token);
        String role = jwtService.extractRole(token);

        if (!userRepository.existsByEmail(email)) {
            sendUnauthorized(response, "User no longer exists");
            return;
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
                objectMapper.writeValueAsString(Map.of("error", message))
        );
    }
}