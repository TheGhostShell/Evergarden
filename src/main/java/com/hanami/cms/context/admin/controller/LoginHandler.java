package com.hanami.cms.context.admin.controller;

import com.hanami.cms.context.admin.domain.jwt.JWTTokenService;
import com.hanami.cms.context.admin.entity.Guest;
import com.hanami.cms.context.admin.entity.GuestMappingInterface;
import com.hanami.cms.context.admin.entity.User;
import com.hanami.cms.context.admin.infrastructure.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Component
public class LoginHandler {

    private UserRepository userRepository;


    public LoginHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<ServerResponse> login(ServerRequest request) {

        Mono<User> userMono = userRepository.findByEmail("violet@mail.com");

        ArrayList<GrantedAuthority> authorities = new ArrayList();
        authorities.add(new SimpleGrantedAuthority("ROLE_GUEST"));
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        String token = JWTTokenService.generateToken("violet", "pass", authorities);

        return ServerResponse.ok().body(BodyInserters.fromObject("Welcome user "+token));
    }

    public Mono<ServerResponse> create(ServerRequest request) {

        return null;
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
