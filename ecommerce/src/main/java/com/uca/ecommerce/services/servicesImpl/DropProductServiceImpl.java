package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.mappers.DropProductMapper;
import com.uca.ecommerce.domain.dto.request.dropProduct.CreateDropProductRequest;
import com.uca.ecommerce.domain.dto.response.DropProductResponse;
import com.uca.ecommerce.domain.entities.Drop;
import com.uca.ecommerce.domain.entities.DropProduct;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.exceptions.FieldAlreadyExistsException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.DropProductRepository;
import com.uca.ecommerce.repository.DropRepository;
import com.uca.ecommerce.repository.ProductRepository;
import com.uca.ecommerce.services.DropProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DropProductServiceImpl implements DropProductService {

    private final DropProductRepository dropProductRepository;
    private final DropRepository dropRepository;
    private final ProductRepository productRepository;
    private final DropProductMapper dropProductMapper;

    @Override
    public List<DropProductResponse> getAllDropProducts() {
        return dropProductMapper.toDtoList(dropProductRepository.findAll());
    }

    @Override
    public List<DropProductResponse> getDropProductsByDropId(UUID dropId) {
        if (!dropRepository.existsById(dropId)) {
            throw new NotFoundException("Drop not found");
        }

        List<DropProduct> dropProducts = dropProductRepository.findByDropId(dropId);
        return dropProductMapper.toDtoList(dropProducts);
    }

    @Override
    public DropProductResponse createDropProduct(CreateDropProductRequest request) {
        Drop drop = dropRepository.findById(request.getDropId())
                .orElseThrow(() -> new NotFoundException("Drop not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        if (dropProductRepository.existsByDropIdAndProductId(request.getDropId(), request.getProductId())) {
            throw new FieldAlreadyExistsException("Product is already assigned to this drop");
        }

        DropProduct saved = dropProductRepository.save(
                dropProductMapper.toEntityCreate(drop, product));
        return dropProductMapper.toDto(saved);
    }

    @Override
    public DropProductResponse deleteDropProduct(UUID id) {
        DropProduct existing = dropProductRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Drop product not found"));

        dropProductRepository.deleteById(id);
        return dropProductMapper.toDto(existing);
    }
}
