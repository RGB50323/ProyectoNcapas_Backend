package com.uca.ecommerce.exceptions;

public class ProductHasActiveProcessesException extends RuntimeException {
    public ProductHasActiveProcessesException(String message) {
        super(message);
    }
}
