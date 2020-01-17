package com.evergarden.cms.app.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;

public class BodyToJsonNode {

    public Mono<JsonNode> bodyToJson(Mono<DataBuffer> bufferMono) {
        return BodyToJsonNode.bodyToJsonNode(bufferMono);
    }


    public static Mono<JsonNode> bodyToJsonNode(Mono<DataBuffer> buffer) {
        ObjectMapper objectMapper = new ObjectMapper();

        return buffer.flatMap(dataBuffer -> {
            try {
                return Mono.just(objectMapper.readTree(dataBuffer.asInputStream()))
                    .publishOn(Schedulers.elastic());
            } catch (IOException e) {
                return Mono.empty();
            }
        });
    }
}
