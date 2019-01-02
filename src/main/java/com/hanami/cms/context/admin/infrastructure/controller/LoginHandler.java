package com.hanami.cms.context.admin.infrastructure.controller;

import com.hanami.cms.context.admin.application.jwt.JWTTokenService;
import com.hanami.cms.context.admin.domain.entity.*;
import com.hanami.cms.context.admin.infrastructure.persistence.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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
@Log4j2
public class LoginHandler {

    private UserRepository userRepository;


    @Autowired
    public LoginHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        
        request.bodyToMono(String.class).map(s -> {
        	log.info(s);
        	return s;
			}
		).subscribe();
//        User user = new User();
//        user.setFirstname();
//        user.setLastname();

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
