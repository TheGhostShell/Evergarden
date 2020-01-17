package com.evergarden.cms.app.utils;

import java.util.Objects;

public class ExceptionUtils {

    public static Throwable getRootCause(Throwable throwable){
        Objects.requireNonNull(throwable);
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause){
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }
}
