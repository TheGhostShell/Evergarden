package com.evergarden.cms.context.user.domain.exception;

public class InvalidProfileException  extends RuntimeException{
    public InvalidProfileException(String message) {
        super("Profile name is not valid  with value " + message);
    }
}
