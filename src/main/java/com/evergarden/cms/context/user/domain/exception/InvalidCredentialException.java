package com.evergarden.cms.context.user.domain.exception;

public class InvalidCredentialException extends RuntimeException {
    public InvalidCredentialException(String message) {
        super("Invalid credential for user with email " + message);
    }
}
