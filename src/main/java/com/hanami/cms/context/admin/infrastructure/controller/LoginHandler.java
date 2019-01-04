package com.hanami.cms.context.admin.infrastructure.controller;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.runtime.Settings;
import com.hanami.cms.context.admin.application.jwt.JWTTokenService;
import com.hanami.cms.context.admin.domain.entity.*;
import com.hanami.cms.context.admin.infrastructure.persistence.UserRepository;
import com.hanami.cms.context.publisher.domain.entity.PostMappingInterface;
import com.hanami.cms.context.publisher.domain.entity.UpdatedPost;
import lombok.extern.log4j.Log4j2;
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

import java.util.ArrayList;

@Component
public class LoginHandler {

    private UserRepository userRepository;

    private Logger logger;

    @Autowired
    public LoginHandler(UserRepository userRepository, Logger logger) {
        this.userRepository = userRepository;
        this.logger = logger;
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
//        request.body(BodyExtractors.toMono(UpdatedPost.class)).flatMap(s -> {
//            System.out.println("inside");
//            logger.info(s.toString());
//            return null;
//        }).subscribe();

        request.exchange().getRequest().getBody().map(DataBuffer::asByteBuffer).map(byteBuffer -> {
            DslJson<Object> dslJson = new DslJson<>(Settings.withRuntime().includeServiceLoader());
            dslJson.deserialize()
        });

        return request.body(BodyExtractors.toMono(String.class)).flatMap(updatedPost -> {
            System.out.println("fuck all this " + updatedPost);

            Mono<PostMappingInterface> post =  Mono.empty();

            //            Mono<PostMappingInterface> post = repository
            //                    .create(new Post(updatedPost.getTitle(), updatedPost.getBody(), updatedPost.getAuthor()));

            return ServerResponse.ok().body(post, PostMappingInterface.class);
        });
        //request.exchange().getRequest().getBody().map(dataBuffer -> )
//        request.bodyToMono(String.class).map(s -> {
//        	logger.info(s);
//        	return s;
//			}
//		)
//                .doOnError(throwable -> logger.error("error bad"+throwable))
//                .subscribe(s -> System.out.println("finish"+s));
//        User user = new User();
//        user.setFirstname();
//        user.setLastname();

        //return ServerResponse.ok().build();
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
