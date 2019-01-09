package com.hanami.cms.context.admin.infrastructure.persistence;

import com.hanami.cms.context.admin.domain.entity.*;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.davidmoten.rx.jdbc.Database;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.adapter.rxjava.RxJava2Adapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class UserRepository {

    private Database database;

    private Logger logger;

    @Autowired
    public UserRepository(Database database, Logger logger) {
        this.database = database;
        this.logger = logger;
    }

    public Mono<UserMappingInterface> findByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = :email";

        Single<UserMappingInterface> singleUser = database.select(sql)
                .parameter("email", email)
                .autoMap(UserMappingInterface.class)
                .firstOrError();

        return RxJava2Adapter.singleToMono(singleUser);
    }

    public Mono<UserMappingInterface> findById(int id) {
        String sql = "SELECT * FROM user WHERE id = :id";

        Single<UserMappingInterface> singleUser = database.select(sql)
                .parameter("id", id)
                .autoMap(UserMappingInterface.class)
                .firstOrError()
                .doOnError(throwable -> logger.error("no user " + throwable));

        return RxJava2Adapter.singleToMono(singleUser);
    }

    //Todo refactor
    public Mono<Role> createUserRole(Role role, int userId) {
        String createUserRoleSql = "INSERT INTO user_roles (user_id, roles_id) VALUES(:userId, :roleId)";

        return createRole(role)
                .flatMap(role1 -> {
                    database.update(createUserRoleSql)
                            .parameter("roleId", role1.getId())
                            .parameter("userId", userId)
                            .returnGeneratedKeys()
                            .getAs(Integer.class)
                            .doOnError(throwable -> logger.error("Error to associate role to user " + throwable))
                            .subscribe();

                    return Mono.just(role1);
                });

        //return findRoleByCriteria(role.toString(), userId);
    }

    public Mono<Role> createRole(Role role) {
        String createRole = "INSERT INTO role (role) VALUES(:role)";

        return findRole(role)
                .doOnError(throwable -> {
                    logger.error("Try to create role: " + role.getRoleValue()+ " in table role");
                    database.update(createRole)
                            .parameter("role", role.getRoleValue())
                            .returnGeneratedKeys()
                            .getAs(Integer.class)
                            .doOnError(throwable1 -> logger.error("Can't insert role into Role table" + throwable1))
                            .subscribe();
                })
                .flatMap(role1 -> findRole(role));
    }

    public Mono<Role> findRole(Role role) {
        String findRole = "SELECT * FROM role WHERE role = :role";

        Single<Role> singleRole = database.select(findRole)
                .parameter("role", role.getRoleValue())
                .get(rs -> {
                    Role r = Role.createFromRawValue(rs.getString("role"));
                    r.setId(rs.getInt("id"));
                    return r;
                })
                .firstOrError();

        return RxJava2Adapter.singleToMono(singleRole);
    }

    //Todo refactor
//    public Mono<Role> findRoleByCriteria(String role, int userId) {
//        String sql = "SELECT * FROM user_roles WHERE role = :role AND user_id = :userId";
//
//        Single<Role> roleSingle = database.select(sql)
//                .parameter("role", role)
//                .parameter("user_id", userId)
//                .get(rs -> {
//                    Role r = new Role(rs.getInt("user_id"), rs.getString("role"));
//                    return r;
//                })
//                .firstOrError();
//
//        return RxJava2Adapter.singleToMono(roleSingle);
//    }


    public Mono<UserMappingInterface> create(UserMappingInterface user) {
        String createUserSql = "INSERT INTO user (email, password, firstname, lastname, activated, salt) " +
                "VALUES (:email, :password, :firstname, :lastname, :activated, :salt) ";

        Flowable<Integer> record = database.update(createUserSql)
                .parameter("email", user.getEmail())
                .parameter("password", user.getPassword())
                .parameter("firstname", user.getFirstName())
                .parameter("lastname", user.getLastName())
                .parameter("activated", user.isActivated())
                .parameter("salt", user.getSalt())
                .returnGeneratedKeys()
                .getAs(Integer.class);

        Single<UserMappingInterface> singleUser = record.map(userId -> {

            Flux<Role> flux = Flux.fromStream(user.getRoles()
                    .stream());

            flux.map(role -> createUserRole(role, userId).subscribe())
                    .subscribe();

            return userId;

        }).flatMap(integer -> findById(integer)).firstOrError();

        return RxJava2Adapter.singleToMono(singleUser);
    }

    //Todo refactor
    public Mono<UserMappingInterface> findByRole(Role role) {
        String sql = "SELECT u.id, u.email, u.firstname, u.lastname, u.password, u.salt, u.activated, r.role " +
                "FROM user u " +
                "INNER JOIN  user_roles ur ON u.id = ur.user_id " +
                "INNER JOIN role r ON r.id = ur.roles_id WHERE r.role = :role ";

        Single<UserMappingInterface> singleUser = database.select(sql)
                .parameter("role", role.getRoleValue())
                .get(rs -> {
                    User user = new User();

                    user.setId(rs.getInt("id"))
                            .setLastname(rs.getString("lastname"))
                            .setFirstname(rs.getString("firstname"))
                            .setEmail(rs.getString("email"))
                            .setActivated(rs.getBoolean("activated"))
                            .setEncodedCredential(new EncodedCredential(rs.getString("salt"), rs.getString("password")))
                            .addRole(Role.createFromRawValue(rs.getString("role")));

                    return (UserMappingInterface) user;
                })
                .firstOrError();

        return RxJava2Adapter.singleToMono(singleUser);
    }
}
