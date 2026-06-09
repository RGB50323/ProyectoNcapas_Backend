package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.mappers.StockAlertMapper;
import com.uca.ecommerce.domain.dto.request.stockAlert.CreateStockAlertRequest;
import com.uca.ecommerce.domain.dto.response.StockAlertResponse;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.StockAlert;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.exceptions.FieldAlreadyExistsException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.ProductRepository;
import com.uca.ecommerce.repository.StockAlertRepository;
import com.uca.ecommerce.repository.UserRepository;
import com.uca.ecommerce.services.StockAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockAlertServiceImpl implements StockAlertService {

    private final StockAlertRepository alertRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final StockAlertMapper alertMapper;

    @Override
    public List<StockAlertResponse> getAllAlerts() {
        return alertMapper.toDtoList(alertRepository.findAll());
    }

    @Override
    public List<StockAlertResponse> getAlertsByUserId(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return alertMapper.toDtoList(alertRepository.findByUserUuid(userId)); // ← uuid
    }

    @Override
    public List<StockAlertResponse> getAlertsByProductId(UUID productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        return alertMapper.toDtoList(alertRepository.findByProductId(productId));
    }

    @Override
    public StockAlertResponse getAlertById(UUID id) {
        return alertMapper.toDto(findOrThrow(id));
    }

    @Override
    public StockAlertResponse createAlert(CreateStockAlertRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        // Un usuario no puede tener dos alertas para el mismo producto
        if (alertRepository.existsByUserUuidAndProductId(request.getUserId(), request.getProductId()))
            throw new FieldAlreadyExistsException(
                    "Alert already exists for this user and product"
            );

        return alertMapper.toDto(
                alertRepository.save(alertMapper.toEntityCreate(request, user, product))
        );
    }

    @Override
    public StockAlertResponse deleteAlert(UUID id) {
        StockAlert existing = findOrThrow(id);
        alertRepository.deleteById(id);
        return alertMapper.toDto(existing);
    }

    private StockAlert findOrThrow(UUID id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Stock alert not found"));
    }
}