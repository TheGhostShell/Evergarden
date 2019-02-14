package com.evergarden.cms.context.admin.infrastructure.persistence;

import com.evergarden.cms.context.admin.application.config.DefaultAdminConfig;
import com.evergarden.cms.context.admin.domain.security.EvergardenEncoder;
import com.evergarden.cms.context.admin.domain.entity.EncodedCredential;
import com.evergarden.cms.context.admin.domain.entity.Role;
import com.evergarden.cms.context.admin.domain.entity.UpdatedUser;
import com.evergarden.cms.context.admin.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Environment env;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Here we expect to find two user. The default user created when the app is first launch or if no default user
     * with ROLE_MASTER_ADMIN was found {@link DefaultAdminConfig#createDefaultAdmin()}.
     * <p>
     * Default user was created with:
     * -mail 			'violet@mail.com'
     * -activated 		'true'
     * -role 			'ROLE_MASTER_ADMIN'
     * -firstname 		'Violet'
     * -lastname 		'Evergarden'
     * -password    	 password was encoded and protected by random salt
     * <p>
     * The second user is batou inserted by the test script  '/db/script/createUser.sql'.
     * <p>
     * INSERT INTO evergarden_user (email, firstname, lastname, pseudo, password, salt, activated)
     * VALUES ( 'batou@mail.com', 'batou', 'ranger', 'batou', 'password', 'salt', true );
     */
    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/drop.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/migration/V2018.01.15.17.09.50__init.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/createUser.sql")
    void findByEmail() {

        Collection<Role> roles = new ArrayList<>();
        Role             role1 = (new Role("test_admin")).setId(1);
        roles.add(role1);

        StepVerifier.create(userRepository.findByEmail("batou@mail.com"))
            .expectNextMatches(user -> {
                assertEquals("batou@mail.com", user.getEmail());
                assertEquals("batou", user.getFirstname());
                assertEquals("ranger", user.getLastname());
                assertEquals("batou", user.getPseudo());
                assertEquals("password", user.getPassword());
                assertEquals("salt", user.getSalt());
                assertTrue(user.isActivated());
                assertEquals(roles, user.getRoles());

                return true;
            })
            .verifyComplete();

        StepVerifier.create(userRepository.findByEmail("batou@mail.co"))
            .expectErrorMatches(throwable -> {
                assertEquals("no user with email batou@mail.co", throwable.getMessage());
                return throwable instanceof NoSuchElementException;
            })
            .verify();
    }

    /**
     * Here we create two user after the default user was dropped by the drop script.
     */
    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/drop.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/migration/V2018.01.15.17.09.50__init.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/createViolet.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/createUser.sql")
    void fetchAll() {

        Collection<Role> roles = new ArrayList<>();
        Role             role1 = (new Role("master_admin")).setId(1);
        roles.add(role1);

        StepVerifier.create(userRepository.fetchAll())
            .expectNextMatches(user -> {
                assertEquals("violet@mail.com", user.getEmail());
                assertEquals("Violet", user.getFirstname());
                assertEquals("Evergarden", user.getLastname());
                assertNull(user.getPseudo());
                assertNotNull(user.getPassword());
                assertNotNull(user.getSalt());
                assertTrue(user.isActivated());
                assertEquals(roles, user.getRoles());

                return true;
            })
            .expectNextCount(1L)
            .verifyComplete();
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/drop.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/migration/V2018.01.15.17.09.50__init.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/createUser.sql")
    void findById() {

        Collection<Role> roles = new ArrayList<>();
        Role             role1 = (new Role("test_admin")).setId(1);
        roles.add(role1);

        StepVerifier.create(userRepository.findById(1))
            .expectNextMatches(user -> {
                assertEquals("batou@mail.com", user.getEmail());
                assertEquals("batou", user.getFirstname());
                assertEquals("ranger", user.getLastname());
                assertEquals("batou", user.getPseudo());
                assertEquals("password", user.getPassword());
                assertEquals("salt", user.getSalt());
                assertTrue(user.isActivated());
                assertEquals(roles, user.getRoles());

                return true;
            })
            .verifyComplete();
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/drop.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/migration/V2018.01.15.17.09.50__init.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/createViolet.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/createUser.sql")
    void createUserRole() {

        Role role   = new Role("admin");
        int  userId = 2; // Actually we have two users in db

        StepVerifier.create(userRepository.createUserRole(role, userId))
            .expectNextMatches(freshRole -> {
                assertEquals("ROLE_ADMIN", freshRole.getRoleValue());
                assertEquals(3, freshRole.getId());

                return true;
            })
            .verifyComplete();

        Role expectedRole1 = new Role("test_admin");
        expectedRole1.setId(2);
        Role expectedRole2 = new Role("admin");
        expectedRole2.setId(3);

        HashMap<String, Role> roles = new HashMap<>();
        roles.put(expectedRole1.getRoleValue(), expectedRole1);
        roles.put(expectedRole2.getRoleValue(), expectedRole2);

        /*
         * We need to verify if new role is really associate to the user
         * to do that we fetch the user with the repository
         */
        StepVerifier.create(userRepository.findById(2))
            .expectNextMatches(user -> {

                long result = user.getRoles().stream().peek(role1 -> {
                    Role target = roles.get(role1.getRoleValue());
                    assertNotNull(target);
                    assertTrue(target.equals(role1));
                }).count();

                assertEquals(result, 2L);

                return true;
            })
            .verifyComplete();
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/drop.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/migration/V2018.01.15.17.09.50__init.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/createUser.sql")
    void createRole() {

        StepVerifier.create(userRepository.createRole(new Role("admin")))
            .expectNextMatches(role -> {
                assertEquals("ROLE_ADMIN", role.getRoleValue());
                assertEquals(2, role.getId());

                return true;
            })
            .verifyComplete();

    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/drop.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/migration/V2018.01.15.17.09.50__init.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/createUser.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/db/script/drop.sql")
    void findRole() {
        StepVerifier.create(userRepository.findRole(new Role("TEST_ADMIN")))
            .expectNextMatches(role -> {
                assertEquals("ROLE_TEST_ADMIN", role.getRoleValue());
                assertEquals(1, role.getId());

                return true;
            })
            .verifyComplete();
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/migration/V2018.01.15.17.09.50__init.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/createUser.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/db/script/drop.sql")
    void create() {

        EvergardenEncoder encoder = new EvergardenEncoder(env, logger);

        encoder.encode("HardPAssword9");
        EncodedCredential credential = encoder.getEncodedCredential();

        User userToSave = new User();
        userToSave.setEmail("motoko@section9.net");
        userToSave.setFirstname("Motoko");
        userToSave.setLastname("Kusanagi");
        userToSave.setPseudo("hack9");
        userToSave.setActivated(true);
        userToSave.setEncodedCredential(credential);

        StepVerifier.create(userRepository.create(userToSave))
            .expectNextMatches(userId -> {

                // Maybe this test can be refactor to avoid StepVerifier inside StepVerifier #inception
                StepVerifier.create(userRepository.findById(userId))
                    .expectNextMatches(user -> {

                        assertEquals("motoko@section9.net", user.getEmail());
                        assertEquals("Motoko", user.getFirstname());
                        assertEquals("Kusanagi", user.getLastname());
                        assertEquals("hack9", user.getPseudo());
                        assertEquals(credential.getEncodedPassword(), user.getPassword());
                        assertEquals(credential.getSalt(), user.getSalt());
                        assertTrue(user.isActivated());

                        return true;
                    })
                    .verifyComplete();

                return true;
            })
            .verifyComplete();
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/migration/V2018.01.15.17.09.50__init.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/createUser.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/db/script/drop.sql")
    void findFirstByRole() {
        StepVerifier.create(userRepository.findFirstByRole(new Role("test_admin")))
            .expectNextMatches(user -> {

                assertEquals("batou@mail.com", user.getEmail());
                assertEquals("batou", user.getFirstname());
                assertEquals("ranger", user.getLastname());
                assertEquals("batou", user.getPseudo());
                assertEquals("password", user.getPassword());
                assertEquals("salt", user.getSalt());
                assertTrue(user.isActivated());

                return true;
            })
            .verifyComplete();
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/migration/V2018.01.15.17.09.50__init.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "/db/script/createUser.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "/db/script/drop.sql")
    void update() {

        UpdatedUser userToUpdate = new UpdatedUser();
        userToUpdate.setEmail("motoko@section9.net");
        userToUpdate.setFirstname("Motoko");
        userToUpdate.setLastname("Kusanagi");
        userToUpdate.setPseudo("hack9");
        userToUpdate.setPassword("badpass");
        userToUpdate.setActivated(true);
        userToUpdate.setId(1);

        // Credential are encoded randomly we can't predict the result

        StepVerifier.create(userRepository.update(userToUpdate))
            .expectNextMatches(user -> {

                assertEquals("motoko@section9.net", user.getEmail());
                assertEquals("Motoko", user.getFirstname());
                assertEquals("Kusanagi", user.getLastname());
                assertEquals("hack9", user.getPseudo());
                assertNotNull(user.getPassword());
                assertNotNull(user.getSalt());
                assertTrue(user.isActivated());

                return true;
            }).verifyComplete();
    }
}