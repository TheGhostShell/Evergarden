package com.evergarden.cms.context.user.infrastructure.controller;

import com.evergarden.cms.context.publisher.infrastructure.persistence.PostRepository;
import com.evergarden.cms.context.user.application.service.CRUDProfileService;
import com.evergarden.cms.context.user.application.service.CRUDRoleService;
import com.evergarden.cms.context.user.domain.entity.Profile;
import com.evergarden.cms.context.user.domain.entity.Role;
import com.evergarden.cms.context.user.infrastructure.controller.input.ProfileRequest;
import com.evergarden.cms.context.user.infrastructure.controller.input.RoleNameRequest;
import com.evergarden.cms.context.user.infrastructure.persistence.ProfileRepository;
import com.evergarden.cms.context.user.infrastructure.persistence.RoleRepository;
import com.evergarden.cms.context.user.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
@WebFluxTest
class ProfileRouterTest {
    @MockBean
    private RoleRepository roleRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private ProfileRepository profileRepository;
    @MockBean
    private PostRepository postRepository;
    @MockBean
    CRUDProfileService crudProfileService;
    @MockBean
    CRUDRoleService crudRoleService;
    @Autowired
    private Environment env;
    private WebTestClient client;


    @BeforeEach
    void setup() {
        ProfileHandler profileHandler = new ProfileHandler(crudProfileService, crudRoleService);
        RouterFunction<ServerResponse> router = (new ProfileRouter()).profileRoute(profileHandler,
            env.getProperty("v1s"));
        client = WebTestClient.bindToRouterFunction(router)
            .build();
    }


    @Test
    void should_create_new_profile_http() {

        RoleNameRequest roleAdmin = new RoleNameRequest("admin");
        RoleNameRequest roleWriter = new RoleNameRequest("writer");

        Role roleAdminBdd = new Role("id-admin", "admin");
        Role roleWriterBdd = new Role("id-writer", "writer");

        ArrayList<Role> rolesBdd = new ArrayList<>();
        rolesBdd.add(roleAdminBdd);
        rolesBdd.add(roleWriterBdd);

        ArrayList<RoleNameRequest> rolesReq = new ArrayList<>();
        rolesReq.add(roleAdmin);
        rolesReq.add(roleWriter);

        List<Role> roles = rolesReq.stream()
            .map(roleNameRequest -> new Role(roleNameRequest.getRole()))
            .collect(Collectors.toList());

        ProfileRequest profileRequest = ProfileRequest.builder()
            .name("ADMINISTRATOR")
            .roles(rolesReq)
            .build();

        Profile profilesFromBdd = Profile.builder()
            .id("id-profile")
            .name("ADMINISTRATOR")
            .roles(rolesBdd)
            .build();

        BDDMockito.given(crudRoleService.saveRoles(roles))
            .willReturn(Mono.just(rolesBdd));

        BDDMockito.when(crudProfileService.save(Profile.builder()
            .name("ADMINISTRATOR")
            .roles(rolesBdd)
            .build()))
            .thenReturn(Mono.just(profilesFromBdd));

        client.post()
            .uri(env.getProperty("v1s") + "/profile")
            .bodyValue(profileRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(Profile.class)
            .consumeWith(profileEntityExchangeResult -> {
                Profile profile = profileEntityExchangeResult.getResponseBody();
                Assertions.assertEquals("id-profile", profile.getId());
                Assertions.assertEquals("ADMINISTRATOR", profile.getName());
                Assertions.assertEquals(rolesBdd, profile.getRoles());
            });
    }
}
