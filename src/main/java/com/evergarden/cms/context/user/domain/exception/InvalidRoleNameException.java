package com.evergarden.cms.context.user.domain.exception;

public class InvalidRoleNameException extends RuntimeException {

    public InvalidRoleNameException(String message) {
        super("Invalid role name with value: "+message);
    }
}
