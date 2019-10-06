package com.evergarden.cms.context.user.application.service;

import com.evergarden.cms.context.user.domain.entity.Guest;
import com.evergarden.cms.context.user.domain.entity.RoleEnum;
import com.evergarden.cms.app.config.security.JwtHelper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Service
public class GenerateGuestTokenService {

    private Logger logger;
    private JwtHelper jwtHelper;

    @Autowired
    public GenerateGuestTokenService(Logger logger, JwtHelper jwtHelper) {
        this.logger = logger;
        this.jwtHelper = jwtHelper;
    }

    public Mono<Guest> generateGuestToken(Mono<Guest> guestMono){
        // TODO extract email from request if present return error if no subject field filled
        return guestMono
            .flatMap(guest -> {
                logger.debug("Generating guest token for subject "+ guest.getSubject());
                ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(RoleEnum.GUEST.name()));
                String subject = guest.getSubject();
                Guest guest1 = new Guest();
                if (guest.getSubject() == null) {
                    subject = "unknown";
                }
                logger.debug("Guest subject is "+subject);
                String token = jwtHelper.generateToken(subject, authorities, 1L, "").getToken();
                guest1.setToken(token);
                guest1.setSubject(subject);
                logger.debug("New token generated is " + token);
                return Mono.just(guest1);
            });
    }
}
