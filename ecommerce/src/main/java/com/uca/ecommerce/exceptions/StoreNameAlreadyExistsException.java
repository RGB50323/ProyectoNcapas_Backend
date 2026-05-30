package com.uca.ecommerce.exceptions;

public class StoreNameAlreadyExistsException extends RuntimeException {
    public StoreNameAlreadyExistsException(String message) {
        super(message);
    }
}
