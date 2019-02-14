package com.evergarden.cms.context.admin.infrastructure.persistence;

import io.reactivex.Single;
import org.davidmoten.rx.jdbc.Database;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.adapter.rxjava.RxJava2Adapter;
import reactor.core.publisher.Mono;

@Component
public class RoleRepository {
    private Database db;

    @Autowired
    public RoleRepository(Database db) {
        this.db = db;
    }

    public Mono<Integer> deleteRoleByUserId(int id) {
        String deleteSql = "DELETE FROM evergarden_user_roles WHERE user_id = :id";

        Single<Integer> single = db.update(deleteSql)
            .parameter("id", id)
            .counts().firstOrError();

        return RxJava2Adapter.singleToMono(single);
    }
}
