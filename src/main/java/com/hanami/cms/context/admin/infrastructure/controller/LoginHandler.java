package com.hanami.cms.context.admin.infrastructure.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanami.cms.context.admin.application.jwt.EvergardenEncoder;
import com.hanami.cms.context.admin.application.jwt.JWTTokenService;
import com.hanami.cms.context.admin.domain.entity.*;
import com.hanami.cms.context.admin.infrastructure.persistence.UserRepository;
import org.h2.jdbc.JdbcSQLException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

@Component
public class LoginHandler {

    private UserRepository userRepository;

    private Logger logger;

    private ObjectMapper objectMapper;

    private EvergardenEncoder encoder;

    @Autowired
    public LoginHandler(UserRepository userRepository, Logger logger, ObjectMapper objectMapper,
                        EvergardenEncoder encoder) {
        this.userRepository = userRepository;
        this.logger = logger;
        this.objectMapper = objectMapper;
        this.encoder = encoder;
    }

    public Mono<ServerResponse> login(ServerRequest request) {

        Mono<UserMappingInterface> userMono = userRepository.findByEmail("violet@mail.com");

        ArrayList<GrantedAuthority> authorities = new ArrayList();
        authorities.add(new SimpleGrantedAuthority("ROLE_GUEST"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        String token = JWTTokenService.generateToken("violet", "pass", authorities);

        return ServerResponse.ok()
                .body(BodyInserters.fromObject(new Token(token)));
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        logger.info("start private user");

        return request.bodyToMono(DataBuffer.class)
                .flatMap(dataBuffer -> {
                    try {
                        return Mono.just(objectMapper.readTree(dataBuffer.asInputStream()))
                                .publishOn(Schedulers.elastic());
                    } catch (IOException e) {
                        return Mono.empty();
                    }
                })
                .flatMap(jsonNode -> {

                    try {

                        encoder.encode(jsonNode.get("user").get("password").asText());

                        User user = new User();
                        user.setEmail(jsonNode.get("user").get("email").asText());
                        user.setFirstname(jsonNode.get("user").get("firstname").asText());
                        user.setLastname(jsonNode.get("user").get("lastname").asText());
                        user.setPseudo(jsonNode.get("user").get("pseudo").asText());
                        user.setEncodedCredential(encoder.getEncodedCredentials());
                        Iterator<JsonNode> it = jsonNode.get("user").get("roles").elements();

                        while (it.hasNext()) {
                            user.addRole(new Role(it.next().asText()));
                        }

                        return userRepository.create(user)
                                .onErrorReturn(user)
                                .flatMap(userMappingInterface -> {
                                    if (userMappingInterface.getId() > 0) {
                                        return ServerResponse.ok().body(Mono.just(userMappingInterface), UserMappingInterface.class);
                                    } else {
                                        return ServerResponse.status(HttpStatus.CONFLICT).build();
                                    }
                                });

                    } catch (NullPointerException e) {
                        return ServerResponse.badRequest().build();
                    }
                });
    }

    /**
     * Some resource it exposed for visitor and we need to know you want visit without authenticate so
     * this resource will create a token for anonymous visitor with 5 minutes expiration date
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> guest(ServerRequest request) {
        return request.body(BodyExtractors.toMono(Guest.class))
                .flatMap(guest -> {
                    String token = JWTTokenService.generateGuestToken(guest.getSubject());
                    guest.setToken(token);
                    return ServerResponse.ok()
                            .body(Mono.just(guest), Guest.class);
                });
    }
    
    public Mono<ServerResponse> read(ServerRequest request) {
        
        
        Mono<UserMappingInterface> userMono = userRepository.findById(new Integer(request.pathVariable("id")));
        
        return ServerResponse.ok().body(userMono, UserMappingInterface.class);
    }
}
