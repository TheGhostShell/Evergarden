package com.hanami.cms.context.admin.infrastructure.persistence;

import com.hanami.cms.context.admin.application.jwt.EvergardenEncoder;
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
                .doOnError(throwable -> logger.info("no user "+ throwable ));

        return RxJava2Adapter.singleToMono(singleUser);
    }

    public Mono<Role> createRole(RoleEnume role, int userId) {
        String createUserRoleSql = "INSERT INTO user_roles (user_id, role) VALUES(:userId, :role)";
        logger.info("creating inserting role");
        database.update(createUserRoleSql)
                .parameter("role", role.toString())
                .parameter("userId", userId)
                .counts()
                .doOnError(throwable -> logger.info("no data to insert user roles"+throwable))
                .subscribe();

        return findRoleByCriteria(role.toString(), userId);
    }

    public Mono<Role> findRoleByCriteria(String role, int userId) {
        String sql = "SELECT * FROM user_roles WHERE role = :role AND user_id = :userId";

        Single<Role> roleSingle = database.select(sql)
                .parameter("role", role)
                .parameter("user_id", userId)
                .get(rs -> {
                    Role r = new Role(
                            rs.getInt("user_id"),
                            rs.getString("role")
                    );
                    return r;
                })
                .firstOrError();

        return RxJava2Adapter.singleToMono(roleSingle);
    }

    public Mono<UserMappingInterface> create(UserMappingInterface user) {
        String createUserSql = "INSERT INTO user (email, password, firstname, lastname, activated, salt) " +
				"VALUES(:email, :password, :firstname, :lastname, :activated, :salt) ";

        logger.info("Trying to create an admin");

        Flowable<Integer> record = database.update(createUserSql)
                .parameter("email", user.getEmail())
                .parameter("password", user.getPassword())
                .parameter("firstname", user.getFirstName())
                .parameter("lastname", user.getLastName())
                .parameter("activated", user.isActivated())
                .parameter("salt", user.getSalt())
                .returnGeneratedKeys()
                .getAs(Integer.class);
//                .transacted()
//                .counts()
//                .flatMap(integerTx -> {
//
//                    if (integerTx.isValue()) {
//                        logger.info("try to create role "+ integerTx.value().intValue());
//                        Flux<RoleEnume> flux = Flux.fromStream(user.getRoles().stream());
//                        logger.info("stream covert to flux " +user.getRoles().get(0).toString());
//                        flux.flatMap(role -> {
//                                    logger.info("inset "+role.toString());
//                                    return createRole(role, integerTx.value());
//                                }
//
//
////                            integerTx.update(createUserRoleSql)
////                                    .parameter("userId", integerTx.value().intValue())
////                                    .parameter("role", role.toString())
////                                    .valuesOnly()
////                                    .counts()
//                        ).doOnError(throwable -> {
//                            logger.error("shit something bad appends");
//                        }).subscribe();
//                    }
//                    logger.info("what is the value after used integerx " + integerTx.value());
//                    return Observable.just(integerTx.value()).toFlowable(BackpressureStrategy.BUFFER);
//                }).doOnError(throwable -> {
//                    logger.info("where is the null pointer ");
//                    Arrays.stream(throwable.getStackTrace()).flatMap(stackTraceElement -> {
//                        //logger.info(stackTraceElement.getMethodName());
//                        return null;
//                    }).count();
//                });

        Single<UserMappingInterface> singleUser = record
                .flatMap(userId -> {
            Flux<RoleEnume> flux = Flux.fromStream(user.getRoles().stream());
            logger.info("stream covert to flux " + user.getRoles().get(0).toString());
            flux.map(role -> {
                logger.info("inset " + role.toString());
                return createRole(role, userId);
            }).subscribe();
            return findById(userId);
        }).firstOrError();

        return RxJava2Adapter.singleToMono(singleUser);
    }

    public Mono<UserMappingInterface> findByRole(RoleEnume role) {
        String sql = "SELECT u.id, u.email, u.firstname, u.lastname, u.password, u.salt, u.activated FROM user u " +
				"INNER JOIN " + "user_roles ur ON u.id = ur.user_id " + "WHERE ur.role = :role";

        Single<UserMappingInterface> singleUser = database.select(sql)
                .parameter("role", role.toString())
                .get(rs -> {
                    User user = new User();

                    user.setId(rs.getInt("id"))
                            .setLastname(rs.getString("lastname"))
                            .setFirstname(rs.getString("firstname"))
                            .setEmail(rs.getString("email"))
                            .setActivated(rs.getBoolean("activated"))
                            .setEncodedCredential(new EncodedCredential(rs.getString("salt"), rs.getString("password")))
                            .addRole(RoleEnume.MASTER_ADMIN);

                    return (UserMappingInterface) user;
                })
                .firstOrError();

        return RxJava2Adapter.singleToMono(singleUser);
    }
}
