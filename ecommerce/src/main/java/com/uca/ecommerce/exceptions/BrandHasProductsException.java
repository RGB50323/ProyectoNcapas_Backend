package com.uca.ecommerce.exceptions;

public class BrandHasProductsException extends RuntimeException {
    public BrandHasProductsException(String message) {
        super(message);
    }
}
