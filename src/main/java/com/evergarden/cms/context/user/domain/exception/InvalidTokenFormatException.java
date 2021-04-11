package com.evergarden.cms.context.user.domain.exception;

public class InvalidTokenFormatException extends RuntimeException{
    public InvalidTokenFormatException(){super("Token header not formatted correctly");}
}
