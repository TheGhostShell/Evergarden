package com.evergarden.cms.context.user.application.service;

import com.evergarden.cms.app.config.security.JwtHelper;
import com.evergarden.cms.context.user.domain.entity.Guest;
import com.evergarden.cms.context.user.domain.entity.RoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Service
@Slf4j
public class GenerateGuestTokenService {

    private JwtHelper jwtHelper;

    @Autowired
    public GenerateGuestTokenService(JwtHelper jwtHelper) {
        this.jwtHelper = jwtHelper;
    }

    public Mono<Guest> generateGuestToken(Guest guestMono){
        // TODO extract email from request if present return error if no subject field filled
        return Mono.just(guestMono)
            .flatMap(guest -> {
                log.debug("Generating guest token for subject "+ guest.getSubject());
                ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(RoleEnum.GUEST.name()));
                String subject = guest.getSubject();
                Guest guest1 = new Guest();
                if (guest.getSubject() == null) {
                    subject = "unknown";
                }
                log.debug("Guest subject is "+subject);
                String token = jwtHelper.generateToken(subject, authorities, 1L, "").getToken();
                guest1.setToken(token);
                guest1.setSubject(subject);
                log.debug("New token generated is " + token);
                return Mono.just(guest1);
            });
    }
}
