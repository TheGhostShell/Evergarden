package com.evergarden.cms.context.admin.infrastructure.persistence;

import com.evergarden.cms.context.admin.domain.security.EvergardenEncoder;
import com.evergarden.cms.context.admin.domain.entity.*;
import io.reactivex.Flowable;
import io.reactivex.Single;
import org.davidmoten.rx.jdbc.Database;
import org.davidmoten.rx.jdbc.SelectBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.adapter.rxjava.RxJava2Adapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.NoSuchElementException;

@Component
public class UserRepository {

    private EvergardenEncoder encoder;

    private Database database;

    private Logger logger;

    private RoleRepository roleRepository;

    @Autowired
    public UserRepository(EvergardenEncoder encoder, Database database, Logger logger, RoleRepository roleRepository) {
        this.encoder = encoder;
        this.database = database;
        this.logger = logger;
        this.roleRepository = roleRepository;
    }

    public Mono<UserMappingInterface> findByEmail(String email) {
        logger.warn("find by email test");
        String sql = "SELECT u.id, u.email, u.firstname, u.lastname, u.pseudo, u.activated, u.salt, u.password, " +
            "GROUP_CONCAT(DISTINCT CONCAT(r.id, ':', r.role)) AS concat_role " +
            "FROM evergarden_user u " +
            "INNER JOIN evergarden_user_roles ur on u.id = ur.user_id " +
            "INNER JOIN evergarden_role r on ur.role_id = r.id " +
            "WHERE u.email = :email";

        return noCacheFix(email)
            .flatMap(aBoolean -> {
                if (aBoolean) {

                    SelectBuilder builder = database.select(sql)
                        .parameter("email", email);

                    Single<UserMappingInterface> singleUser = userMap(builder).firstOrError();

                    return RxJava2Adapter.singleToMono(singleUser);
                }
                return Mono.error(new NoSuchElementException("no user with email " + email));
            });
    }

    public Flux<UserMappingInterface> fetchAll() {
        String sql = "SELECT u.id, u.email, u.firstname, u.lastname, u.pseudo, u.activated, u.salt, u.password, " +
            "GROUP_CONCAT(DISTINCT CONCAT(r.id, ':', r.role)) AS concat_role " +
            "FROM evergarden_user u " +
            "LEFT JOIN evergarden_user_roles ur on u.id = ur.user_id " +
            "LEFT JOIN evergarden_role r on ur.role_id = r.id " +
            "GROUP BY u.id ";

        SelectBuilder builder = database.select(sql);

        Flowable<UserMappingInterface> flowUser = userMap(builder);

        return Flux.from(flowUser);
    }

    //todo refactor
    public Mono<UserMappingInterface> findById(int id) {

        logger.info("try to find this id " + id);

        String sql = "SELECT u.id, u.email, u.firstname, u.lastname, u.pseudo, u.activated, u.salt, u.password, " +
            "GROUP_CONCAT(DISTINCT CONCAT(r.id, ':', r.role)) AS concat_role " +
            "FROM evergarden_user u " +
            "INNER JOIN evergarden_user_roles ur on u.id = ur.user_id " +
            "INNER JOIN evergarden_role r on ur.role_id = r.id " +
            "WHERE u.id = :id";

        SelectBuilder builder = database.select(sql)
            .parameter("id", id);

        Single<UserMappingInterface> singleUser = userMap(builder)
            .doOnError(throwable -> logger.error(throwable.toString()))
            .firstOrError();

        return RxJava2Adapter.singleToMono(singleUser);
    }

    private Flowable<UserMappingInterface> userMap(SelectBuilder builder) {

        return builder
            .get(rs -> {
                User   u     = new User();
                String roles = rs.getString("concat_role");

                logger.warn("the value is null !! " + roles);

                if (roles != null && !roles.equals(":")) {
                    logger.warn("inside the deep association");
                    Arrays.stream(roles.split(","))
                        .map(s -> {
                            logger.warn(s);
                            String[] role = s.split(":");

                            u.addRole(Role.createFromRawValue(new Integer(role[0]), role[1]));

                            return s;

                        }).count();
                }

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
            });
    }

    //Todo refactor
    public Mono<Role> createUserRole(Role role, int userId) {

        String createUserRoleSql = "INSERT INTO evergarden_user_roles (user_id, role_id) VALUES(:userId, :roleId)";

        logger.warn("try to creat user role " + role.getRoleValue() + userId);

        return createRole(role)
            .doOnError(throwable -> logger.error(throwable.toString()))
            .flatMap(role1 -> {
                logger.warn("so now we will execute createUserRoleSql ");
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
        String createRole = "INSERT INTO evergarden_role (role) VALUES(:role)";

        Mono<Role> r = findRole(role)
            .doOnError(throwable -> {
                logger.warn("I m not found the role so I will try to create the new role " + role.getRoleValue());
                database.update(createRole)
                    .parameter("role", role.getRoleValue())
                    .returnGeneratedKeys()
                    .getAs(Integer.class)
                    .doOnError(throwable1 -> logger.error(throwable1.toString()))
                    .blockingFirst();
            })
            .onErrorResume(throwable -> {
                logger.warn(throwable.toString());
                return findRole(role);
            });

        return r;
    }

    public Mono<Role> findRole(Role role) {

        String findRole = "SELECT * FROM evergarden_role r WHERE r.role = :role";

        logger.info("finding role " + role.getRoleValue());

        Single<Role> singleRole = database.select(findRole)
            .parameter("role", role.getRoleValue())
            .get(rs -> {
                Role r = Role.createFromRawValue(rs.getString("role"));
                r.setId(rs.getInt("id"));
                logger.warn("the role is roleId :  " + r.getId());
                return r;
            })
            .firstOrError()
            .doOnError(throwable -> logger.error(throwable.toString()));

        return RxJava2Adapter.singleToMono(singleRole);
    }

    //Todo refactor
    public Mono<Role> findRoleByCriteria(Role role, int userId) {
        String sql = "SELECT * FROM user_roles ur WHERE role = :role AND ur.user_id = :userId " +
            "INNER JOIN role r ON r.id = ur.role_id";

        Single<Role> roleSingle = database.select(sql)
                .parameter("role", role.getRoleValue())
                .parameter("user_id", userId)
                .get(rs -> {
                    Role r = new Role(rs.getString("role")).setId(rs.getInt("user_id"));
                    return r;
                })
                .firstOrError();

        return RxJava2Adapter.singleToMono(roleSingle);
    }

    public Mono<Integer> create(UserMappingInterface user) {
        // TODO check if email already exist to avoid side effect incrementation of id when on duplicate email
        logger.warn("inside the target crate user method");
        String createUserSql = "INSERT INTO evergarden_user (email, password, firstname, lastname, activated, salt, pseudo) " +
            "VALUES (:email, :password, :firstname, :lastname, :activated, :salt, :pseudo) ";

        Flowable<Integer> record = database.update(createUserSql)
            .parameter("email", user.getEmail())
            .parameter("password", user.getPassword())
            .parameter("firstname", user.getFirstname())
            .parameter("lastname", user.getLastname())
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

        logger.warn("the integer is " + singleInteger.toString());
        return RxJava2Adapter.singleToMono(singleInteger);
    }

    //Todo refactor
    public Mono<UserMappingInterface> findFirstByRole(Role role) {
        String sql = "SELECT u.id, u.email, u.firstname, u.lastname, u.pseudo, u.password, u.salt, u.activated, r.role " +
            "FROM evergarden_user u " +
            "INNER JOIN evergarden_user_roles ur ON u.id = ur.user_id " +
            "INNER JOIN evergarden_role r ON r.id = ur.role_id WHERE r.role = :role ";

        Single<UserMappingInterface> singleUser = database.select(sql)
            .parameter("role", role.getRoleValue())
            .get(rs -> {
                User user = new User();

                user.setId(rs.getInt("id"))
                    .setLastname(rs.getString("lastname"))
                    .setFirstname(rs.getString("firstname"))
                    .setEmail(rs.getString("email"))
                    .setPseudo(rs.getString("pseudo"))
                    .setActivated(rs.getBoolean("activated"))
                    .setEncodedCredential(new EncodedCredential(rs.getString("salt"), rs.getString("password")))
                    .addRole(Role.createFromRawValue(rs.getString("role")));

                return (UserMappingInterface) user;
            })
            .firstOrError();

        return RxJava2Adapter.singleToMono(singleUser);
    }

    public Mono<UserMappingInterface> update(UpdatedUser user) {
        String sqlUpdate = "UPDATE evergarden_user SET activated = :activated, email = :email, firstname = :firstname, " +
            "lastname = :lastname, password = :password, salt = :salt, pseudo = :pseudo " +
            "WHERE id = :id ";

        encoder.encode(user.getPassword());

        Single<Integer> isUpdated = database.update(sqlUpdate)
            .parameter("email", user.getEmail())
            .parameter("firstname", user.getFirstname())
            .parameter("lastname", user.getLastname())
            .parameter("pseudo", user.getPseudo())
            .parameter("activated", user.isActivated())
            .parameter("password", encoder.getEncodedCredential().getEncodedPassword())
            .parameter("salt", encoder.getEncodedCredential().getSalt())
            .parameter("id", user.getId())
            .counts().firstOrError();

        return RxJava2Adapter.singleToMono(isUpdated)
            .flatMap(isUp ->
                // TODO what we need to do when no roles is given
                roleRepository
                    .deleteRoleByUserId(user.getId())
                    .flatMap(isDel ->

                        // TODO block is nasty need to be refactor
                        Flux.fromStream(user.getRoles().stream())
                            .map(role ->
                                createUserRole(role, user.getId()).block()
                            )
                            .then(findById(user.getId()))
                    )
            );
    }

    private Mono<Boolean> noCacheFix(String email) {
        String sql = "SELECT * FROM evergarden_user u WHERE u.email = :email";

        Single<Boolean> singleBool = database.select(sql)
            .parameter("email", email)
            .get(rs -> true)
            .firstOrError()
            .onErrorReturn(throwable -> false);

        return RxJava2Adapter.singleToMono(singleBool);
    }
}
