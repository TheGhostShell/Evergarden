package com.evergarden.cms.context.user.domain.exception;

public class RessourceNotFoundException extends RuntimeException {
    public RessourceNotFoundException(String message) {
        super("Resource not found for "+ message);
    }
}
