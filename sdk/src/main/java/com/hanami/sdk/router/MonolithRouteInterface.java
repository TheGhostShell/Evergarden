package com.hanami.sdk.router;

import java.util.function.Function;

public interface MonolithRouteInterface {

    Function<Request, Response> handler = null;

    String getPath();

    HttpMethod getMethod();

    Function<Request, Response> getHandler();

}
