package com.hanami.sdk.router;

import java.util.function.Function;

public interface MonolithRouteInterface {

    Function<ServerRequest, ServerResponse> handler = null;

    String getPath();

    HttpMethod getMethod();

    Function<ServerRequest, ServerResponse> getHandler();

}
