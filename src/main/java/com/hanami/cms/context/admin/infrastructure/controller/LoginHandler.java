package com.hanami.cms.context.admin.infrastructure.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanami.cms.context.admin.application.jwt.EvergardenEncoder;
import com.hanami.cms.context.admin.application.jwt.JWTTokenService;
import com.hanami.cms.context.admin.domain.entity.*;
import com.hanami.cms.context.admin.infrastructure.controller.response.UserCreateResponse;
import com.hanami.cms.context.admin.infrastructure.controller.response.UserResponse;
import com.hanami.cms.context.admin.infrastructure.persistence.UserRepository;
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
import reactor.core.publisher.Flux;
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
                        encoder.encode(jsonNode.get("user")
                                .get("password")
                                .asText());
                        User user = new User();

                        user.setEmail(jsonNode.get("user")
                                .get("email")
                                .asText())
                                .setFirstname(jsonNode.get("user").get("firstname").asText())
                                .setLastname(jsonNode.get("user").get("lastname").asText())
                                .setPseudo(jsonNode.get("user").get("pseudo").asText())
                                .setEncodedCredential(encoder.getEncodedCredentials());

                        Iterator<JsonNode> it = jsonNode.get("user").get("roles").elements();

                        while (it.hasNext()) {
                            user.addRole(new Role(it.next()
                                    .asText()));
                        }

                        return userRepository.create(user)
                                .flatMap(integer -> {
                                    if (integer > 0) {

                                        return userRepository.findById(integer)
                                                .flatMap(userMappingInterface -> {
                                                    UserCreateResponse userCreateResponse =
                                                            UserCreateResponse.mapToUserResponse(userMappingInterface)
                                                            .dropRoles();
                                                    user.getRoles()
                                                            .stream()
                                                            .peek(role -> {
                                                                userCreateResponse.addRole(role.getRoleValue());
                                                            })
                                                            .count();
                                                    Mono<UserCreateResponse> userResponseMono =
                                                            Mono.just(userCreateResponse);

                                                    return ServerResponse.ok()
                                                            .body(userResponseMono, UserCreateResponse.class);
                                                })
                                                .onErrorResume(throwable -> ServerResponse.badRequest().build());
                                    } else {
                                        return ServerResponse.status(HttpStatus.CONFLICT).build();
                                    }
                                })
                                .onErrorResume(throwable -> ServerResponse.badRequest().build());

                    } catch (NullPointerException e) {
                        return ServerResponse.badRequest()
                                .build();
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

        return userRepository.findById(new Integer(request.pathVariable("id")))
                .flatMap(userMappingInterface -> {

                    UserResponse us = UserResponse.mapToUserResponse(userMappingInterface);

                    return Mono.just(us);
                })
                .flatMap(userResponse -> ServerResponse.ok()
                        .body(Mono.just(userResponse), UserResponse.class))
                .onErrorResume(throwable -> ServerResponse.notFound()
                        .build());
    }

    public Mono<ServerResponse> show(ServerRequest request) {

        Flux<UserResponse> userResponseFlux = userRepository.fetchAll()
                .map(userMappingInterface -> UserResponse.mapToUserResponse(userMappingInterface));

        return ServerResponse.ok()
                .body(userResponseFlux, UserResponse.class);
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        return request
                .body(BodyExtractors.toMono(UpdatedUser.class))
                .flatMap(updatedUser -> userRepository.update(updatedUser))
                .flatMap(userMappingInterface -> ServerResponse.ok().body(Mono
                        .just(UserResponse.mapToUserResponse(userMappingInterface)), UserResponse.class));
    }
}
