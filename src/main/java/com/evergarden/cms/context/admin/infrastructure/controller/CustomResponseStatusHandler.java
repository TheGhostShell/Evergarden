package com.evergarden.cms.context.admin.infrastructure.controller;

import ch.qos.logback.core.util.ContentTypeUtil;
import com.google.common.io.ByteStreams;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Configuration
//@Order(-1)
public class CustomResponseStatusHandler implements  WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        if (ex instanceof ResponseStatusException && ((ResponseStatusException) ex).getStatus() == HttpStatus.NOT_FOUND){
            System.out.println("the message is humm" + ex.getMessage());
            try {
                byte[] bytes = ByteStreams.toByteArray(new FileSystemResource("./template/theme/index.html").getInputStream());

                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                exchange.getResponse().setStatusCode(HttpStatus.BAD_GATEWAY);
                exchange.getResponse().setStatusCode(HttpStatus.OK);
                exchange.getResponse().getHeaders().set("Content-Type","text/html");
                return exchange.getResponse().writeWith(Flux.just(buffer));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return Mono.empty();
    }
}
