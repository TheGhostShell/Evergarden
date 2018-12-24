package com.hanami.cms.context.admin.infrastructure;

import com.hanami.cms.context.admin.entity.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserRepository {

    public Mono<User> findByEmail(String email) {



        return null;
    }
}
