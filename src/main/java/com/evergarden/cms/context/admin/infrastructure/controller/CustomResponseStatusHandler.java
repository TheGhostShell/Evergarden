package com.evergarden.cms.context.admin.infrastructure.controller;

import com.google.common.io.ByteStreams;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@Configuration
@Order(-1)
public class CustomResponseStatusHandler implements  WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        if (ex instanceof ResponseStatusException && ((ResponseStatusException) ex).getStatus() == HttpStatus.NOT_FOUND){
            System.out.println("the message is humm" + ex.getMessage());
            try {
                byte[] bytes = ByteStreams.toByteArray(new ClassPathResource("/public/theme/index.html").getInputStream());

                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                exchange.getResponse().setStatusCode(HttpStatus.BAD_GATEWAY);
                exchange.getResponse().setStatusCode(HttpStatus.OK);
                exchange.getResponse().getHeaders().set("Content-Type","text/html");
                return exchange.getResponse().writeWith(Flux.just(buffer));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println(new ClassPathResource("/public/theme/index.html").);
            //exchange.mutate().request(exchange.getRequest().mutate().path("/index.html").build()).build();
/*            Mono<ServerResponse> htm = ServerResponse.ok()
                    .contentType(MediaType.TEXT_HTML).syncBody(new ClassPathResource("/public/theme/index.html"));
            ServerHttpResponse serverHttpResponse = exchange.getResponse();
            new ClassPathResource("/public/theme/index.html")
            DataBuffer dataBuffer = htm.flatMap(serverResponse -> {
                BodyExtractors.toMono()
               serverResponse
            });
            serverHttpResponse.writeWith(Mono.just(htm));*/
//            exchange.getResponse().writeWith(htm.flatMap(serverResponse -> {
//                serverResponse.
//            }));

        }

        return Mono.empty();
        //return exchange.getResponse().setComplete();
    }
}
