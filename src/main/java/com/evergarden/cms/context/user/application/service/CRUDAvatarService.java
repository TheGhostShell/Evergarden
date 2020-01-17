package com.evergarden.cms.context.user.application.service;

import com.evergarden.cms.app.config.security.JwtHelper;
import com.evergarden.cms.context.user.domain.entity.Avatar;
import com.evergarden.cms.context.user.domain.entity.Token;
import com.evergarden.cms.context.user.infrastructure.persistence.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CRUDAvatarService {

    private JwtHelper jwtHelper;
    private UserRepository userRepository;

    public CRUDAvatarService(JwtHelper jwtHelper, UserRepository userRepository) {
        this.jwtHelper = jwtHelper;
        this.userRepository = userRepository;
    }

    public Mono<Void> saveAvatarForUser(Token token, Avatar avatar) {
        String id = jwtHelper.getIdFromToken(token.getToken());
        return userRepository.findById(id)
            .map(user -> {
                user.setAvatar(avatar);
                userRepository.save(user).subscribe();
                return user;
            })
            .flatMap(user -> Mono.empty());
    }

    public Mono<Void> saveAvatarForUser(String token, Avatar avatar) {
        return saveAvatarForUser(jwtHelper.sanitizeHeaderToken(token), avatar);
    }
}
