package com.diego.customeraccount.shared.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException of(String resource, Object identifier) {
        return new ResourceNotFoundException(
                "No se encontro %s con identificador %s".formatted(resource, identifier));
    }
}