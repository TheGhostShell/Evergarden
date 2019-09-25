package com.evergarden.cms.context.admin.infrastructure.persistence;

import com.evergarden.cms.context.admin.domain.entity.Role;
import io.reactivex.Single;
import org.davidmoten.rx.jdbc.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.adapter.rxjava.RxJava2Adapter;
import reactor.core.publisher.Mono;

@Repository
public interface RoleRepository extends ReactiveMongoRepository<Role, String> {

}
