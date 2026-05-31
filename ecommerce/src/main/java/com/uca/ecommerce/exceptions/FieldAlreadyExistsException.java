package com.uca.ecommerce.exceptions;

public class FieldAlreadyExistsException extends RuntimeException {
    public FieldAlreadyExistsException(String message) {
        super(message);
    }
}
