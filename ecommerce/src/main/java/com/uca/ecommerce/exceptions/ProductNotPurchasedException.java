package com.uca.ecommerce.exceptions;

public class ProductNotPurchasedException extends RuntimeException {
    public ProductNotPurchasedException(String message) {
        super(message);
    }
}
