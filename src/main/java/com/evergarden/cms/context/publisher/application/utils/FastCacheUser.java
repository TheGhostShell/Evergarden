package com.evergarden.cms.context.publisher.application.utils;

import com.evergarden.cms.context.user.application.service.CRUDUserService;
import com.evergarden.cms.context.user.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import javax.cache.Cache;
import java.util.Objects;

@Component
@Slf4j
public class FastCacheUser {

    private CRUDUserService      crudUserService;
    private Cache<String, User>  userMonoCache;

    public FastCacheUser(CRUDUserService crudUserService, Cache<String, User> userMonoCache) {
        this.crudUserService = crudUserService;
        this.userMonoCache = userMonoCache;
    }


    public Mono<User> findUserById(String id) {

        if (Objects.nonNull(userMonoCache.get(id))) {
            log.debug("load from fastCache ".concat(id));
            return Mono.just(userMonoCache.get(id));
        }

        return getUser(id);
    }

    // Much slower
    /*public Mono<User> findUserById(String id) {

        return Optional.ofNullable(userMonoCache.get(id))
            .map(user -> {
                log.debug("load from fastCache ".concat(id));
                return Mono.just(userMonoCache.get(id));
            })
            .orElse(getUser(id));
    }*/

    private Mono<User> getUser(String id) {
        return crudUserService.findUser(id)
            .flatMap(user -> {
                log.debug("load from db ".concat(id));
                userMonoCache.put(id, user);
                return Mono.just(user);
            });
    }
}
