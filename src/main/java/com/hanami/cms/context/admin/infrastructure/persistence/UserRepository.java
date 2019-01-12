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

import java.util.Arrays;

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

    //todo refactor
    public Mono<UserMappingInterface> findById(int id) {
        
        logger.info("try to find this id "+ id);
        
        String sql = "SELECT u.id, u.email, u.firstname, u.lastname, u.pseudo, u.activated, u.salt, u.password, " +
            "GROUP_CONCAT(DISTINCT CONCAT(r.id, ':', r.role)) AS concat_role " +
            "FROM user u " +
            "INNER JOIN user_roles ur on u.id = ur.user_id " +
            "INNER JOIN role r on ur.roles_id = r.id " +
            "WHERE u.id = :id";

        Single<UserMappingInterface> singleUser = database.select(sql)
                .parameter("id", id)
                .get(rs -> {
                    User u = new User();
                    
                    String roles = rs.getString("concat_role");
                    
                    logger.warn("the value is null fuck !! "+roles);
                    
                    if(roles != null){
                        logger.warn("inside the deep association");
                        Arrays.stream(roles.split(","))
                            .map(s -> {
                                logger.warn(s);
                                String[] role = s.split(":");
            
                                u.addRole(Role.createFromRawValue(new Integer(role[0]), role[1]));
            
                                return s;
            
                            }).count();
                    }
                    
                    
                    logger.warn("what is null ??");
                    
                    u.setFirstname(rs.getString("firstname"))
                        .setLastname(rs.getString("lastname"))
                        .setPseudo(rs.getString("pseudo"))
                        .setEmail(rs.getString("email"))
                        .setId(rs.getInt("id"))
                        .setEncodedCredential(
                            new EncodedCredential(rs.getString("salt"), rs.getString("password"))
                        );
                    logger.warn("id is " + u.getId());
                    return (UserMappingInterface) u;
                })
            .doOnError(throwable -> logger.error(throwable.toString()))
                .firstOrError();

        return RxJava2Adapter.singleToMono(singleUser);
    }

    //Todo refactor
    public Mono<Role> createUserRole(Role role, int userId) {
        String createUserRoleSql = "INSERT INTO user_roles (user_id, roles_id) VALUES(:userId, :roleId)";

        return createRole(role)
                .doOnError(throwable -> logger.error(throwable.toString()))
                .flatMap(role1 -> {
                    database.update(createUserRoleSql)
                            .parameter("roleId", role1.getId())
                            .parameter("userId", userId)
                            .returnGeneratedKeys()
                            .getAs(Integer.class)
                            .doOnError(throwable -> logger.error(throwable.toString()))
                            .subscribe();

                    return Mono.just(role1);
                });
    }

    public Mono<Role> createRole(Role role) {
        String createRole = "INSERT INTO role (role) VALUES(:role)";

        return findRole(role)
            .doOnError(throwable -> {
                    database.update(createRole)
                            .parameter("role", role.getRoleValue())
                            .returnGeneratedKeys()
                            .getAs(Integer.class)
                            .doOnError(throwable1 -> logger.error(throwable1.toString()))
                            .subscribe();
                })
            .onErrorResume(throwable -> findRole(role));
    }

    public Mono<Role> findRole(Role role) {
        String findRole = "SELECT * FROM role r WHERE r.role = :role";
        
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


    public Mono<Integer> create(UserMappingInterface user) {
        String createUserSql = "INSERT INTO user (email, password, firstname, lastname, activated, salt, pseudo) " +
                "VALUES (:email, :password, :firstname, :lastname, :activated, :salt, :pseudo) ";

        Flowable<Integer> record = database.update(createUserSql)
                .parameter("email", user.getEmail())
                .parameter("password", user.getPassword())
                .parameter("firstname", user.getFirstName())
                .parameter("lastname", user.getLastName())
                .parameter("activated", user.isActivated())
                .parameter("salt", user.getSalt())
                .parameter("pseudo", user.getPseudo())
                .returnGeneratedKeys()
                .getAs(Integer.class);

        Single<Integer> singleInteger = record.map(userId -> {

            Flux<Role> flux = Flux.fromStream(user.getRoles()
                    .stream());

            flux.map(role -> createUserRole(role, userId).subscribe())
                    .subscribe();
            
            return userId;
            
        }).firstOrError();

        return RxJava2Adapter.singleToMono(singleInteger);
    }

    //Todo refactor
    public Mono<UserMappingInterface> findFirstByRole(Role role) {
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
