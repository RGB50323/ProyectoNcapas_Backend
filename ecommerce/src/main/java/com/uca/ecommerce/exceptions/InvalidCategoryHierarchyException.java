package com.uca.ecommerce.exceptions;

public class InvalidCategoryHierarchyException extends RuntimeException {
    public InvalidCategoryHierarchyException(String message) {
        super(message);
    }
}
