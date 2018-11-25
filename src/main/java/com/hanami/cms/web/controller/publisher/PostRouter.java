package com.hanami.cms.web.controller.publisher;

import com.hanami.cms.router.RouteBuilder;
import com.hanami.sdk.router.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class PostRouter {

    @Bean
    public RouterFunction<ServerResponse> route(PostHandler handler, RouteBuilder builder) {
        try {
            return builder.add(HttpMethod.post, "/v1/private/postHandler", handler::create).build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return  null;
    }
}
