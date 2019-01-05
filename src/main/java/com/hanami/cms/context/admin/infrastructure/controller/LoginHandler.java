package com.hanami.cms.context.admin.infrastructure.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanami.cms.context.admin.application.jwt.JWTTokenService;
import com.hanami.cms.context.admin.domain.entity.*;
import com.hanami.cms.context.admin.infrastructure.persistence.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

@Component
public class LoginHandler {

    private UserRepository userRepository;

    private Logger logger;
    
    private ObjectMapper objectMapper;

    @Autowired
    public LoginHandler(UserRepository userRepository, Logger logger, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.logger = logger;
        this.objectMapper = objectMapper;
    }

    public Mono<ServerResponse> login(ServerRequest request) {

        Mono<UserMappingInterface> userMono = userRepository.findByEmail("violet@mail.com");

        ArrayList<GrantedAuthority> authorities = new ArrayList();
        authorities.add(new SimpleGrantedAuthority("ROLE_GUEST"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        String token = JWTTokenService.generateToken("violet", "pass", authorities);

        return ServerResponse.ok().body(BodyInserters.fromObject(new Token(token)));
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        logger.info("start private user");

        Mono<DataBuffer> dataBufferMono = request
            .bodyToMono(DataBuffer.class)
            .map(dataBuffer -> {
                try {
                    JsonNode jsonNode = objectMapper.readTree(dataBuffer.asInputStream());
                    System.out.println(jsonNode.get("user").get("list").get(1).asText());
                    Iterator<JsonNode> it = jsonNode.get("user").get("list").elements();
                    while (it.hasNext()) {
                        System.out.println(it.next().asText());
                    }
                    System.out.println(jsonNode.get("user").get("list").elements().hasNext());
                    Iterator<Map.Entry<String, JsonNode>> itF = jsonNode.get("user").get("basicObj").fields();
                    while (itF.hasNext()) {
                        Map.Entry<String, JsonNode> me = itF.next();
                        System.out.println(me.getKey() + "  -  " + me.getValue().asText());
                    }
                    System.out.println(jsonNode.get("user").get("basicObj").isArray());
                    System.out.println(jsonNode.get("user").get("basicObj").isObject());                } catch (IOException e) {
                    e.printStackTrace();
                }
                return dataBuffer;
            });
        
        return dataBufferMono.flatMap(dataBuffer -> {
            return ServerResponse.ok().build();
        });
        
//        return request
//            .body(BodyExtractors.toMono(DataBuffer.class))
//            .flatMap(dataBuffer -> {
//            try {
//                JsonNode jsonNode = new ObjectMapper().readTree(dataBuffer.asInputStream());
//                Iterator<JsonNode> it = jsonNode.get("user").get("list").elements();
//                while (it.hasNext()) {
//                    System.out.println(it.next().asText());
//                }
//                System.out.println(jsonNode.get("user").get("list").elements().hasNext());
//                Iterator<Map.Entry<String, JsonNode>> itF = jsonNode.get("user").get("basicObj").fields();
//                while (itF.hasNext()) {
//                    Map.Entry<String, JsonNode> me = itF.next();
//                    System.out.println(me.getKey() + "  -  " + me.getValue().asText());
//                }
//                System.out.println(jsonNode.get("user").get("basicObj").isArray());
//                System.out.println(jsonNode.get("user").get("basicObj").isObject());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return ServerResponse.ok().build();
//        });
        
        /*flatMap(jsonString -> {
        
            try {
                JsonNode jsonNode = new ObjectMapper().readTree(jsonString.asInputStream());
                System.out.println(jsonNode.get("age").asInt());
            } catch (IOException e) {
                e.printStackTrace();
            }
            logger.info(" block ?");
            // Mono<PostMappingInterface> post =  Mono.empty();
        
            //            Mono<PostMappingInterface> post = repository
            //                    .create(new Post(updatedPost.getTitle(), updatedPost.getBody(), updatedPost.getAuthor()));
        
            return ServerResponse.ok().build();
        });*/
    }

    /**
     * Some resource it exposed for visitor and we need to know you want visit without authenticate so
     * this resource will create a token for anonymous visitor with 5 minutes expiration date
     * @param request
     * @return
     */
    public Mono<ServerResponse> guest(ServerRequest request) {
        return request.body(BodyExtractors.toMono(Guest.class))
                .flatMap(guest -> {
                   String token = JWTTokenService.generateGuestToken(guest.getSubject());
                   guest.setToken(token);
                   return ServerResponse.ok().body(Mono.just(guest), Guest.class);
                });
    }
}
