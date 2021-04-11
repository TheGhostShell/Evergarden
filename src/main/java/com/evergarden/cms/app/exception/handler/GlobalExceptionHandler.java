package com.evergarden.cms.app.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Component
@Order(-2)
@Slf4j
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {


    public GlobalExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties,
                                  ApplicationContext applicationContext, ServerCodecConfigurer codecConfigurer) {
        super(errorAttributes, resourceProperties, applicationContext);
        setMessageWriters(codecConfigurer.getWriters());
        setMessageReaders(codecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), request -> {
            Throwable error = errorAttributes.getError(request);
            log.error(error.toString());
            return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .bodyValue(error.getMessage());
        });

    }
}
