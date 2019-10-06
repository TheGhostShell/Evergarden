package com.evergarden.cms.context.publisher.domain.exception;

public class RessourceNotFoundException extends RuntimeException {
    public RessourceNotFoundException(String message) {
        super("Ressource not found for Post with id "+message);
    }
}
