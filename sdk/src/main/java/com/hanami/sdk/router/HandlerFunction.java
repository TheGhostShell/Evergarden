package com.hanami.sdk.router;

import java.util.function.Function;

public interface HandlerFunction extends Function {

    /**
     * A handler function is a simple function that handle a object request and return a response
     *
     * @param request a simple object which represent the client request
     * @return ServerResponse
     */
    ServerResponse handle(ServerRequest request);
}
