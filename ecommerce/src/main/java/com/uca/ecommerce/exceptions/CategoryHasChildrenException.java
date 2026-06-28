package com.uca.ecommerce.exceptions;

public class CategoryHasChildrenException extends RuntimeException {
    public CategoryHasChildrenException(String message) {
        super(message);
    }
}