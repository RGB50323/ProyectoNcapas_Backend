package com.uca.ecommerce.exceptions;

public class VerificationLockedException extends RuntimeException {
    public VerificationLockedException(String message) {
        super(message);
    }
}
