package com.hanami.sdk.router;

import java.util.function.Function;

public interface Handler extends Function<Request, Response> {
}
