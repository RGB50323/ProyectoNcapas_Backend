package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.response.DropProductResponse;
import com.uca.ecommerce.domain.entities.Drop;
import com.uca.ecommerce.domain.entities.DropProduct;
import com.uca.ecommerce.domain.entities.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DropProductMapper {

    public DropProduct toEntityCreate(Drop drop, Product product) {
        return DropProduct.builder()
                .drop(drop)
                .product(product)
                .build();
    }

    public DropProductResponse toDto(DropProduct dropProduct) {
        return DropProductResponse.builder()
                .id(dropProduct.getId())
                .dropId(dropProduct.getDrop().getId())
                .dropTitle(dropProduct.getDrop().getTitle())
                .productId(dropProduct.getProduct().getId())
                .productName(dropProduct.getProduct().getName())
                .productSku(dropProduct.getProduct().getSku())
                .build();
    }

    public List<DropProductResponse> toDtoList(List<DropProduct> dropProducts) {
        return dropProducts.stream()
                .map(this::toDto)
                .toList();
    }
}
