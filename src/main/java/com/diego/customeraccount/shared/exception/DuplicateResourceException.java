package com.diego.customeraccount.shared.exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public static DuplicateResourceException of(String field, Object value) {
        return new DuplicateResourceException(
                "Ya existe un registro con %s = %s".formatted(field, value));
    }
}