package com.evergarden.cms.context.user.application.service;

import com.evergarden.cms.context.user.domain.entity.Profile;
import com.evergarden.cms.context.user.domain.exception.RessourceNotFoundException;
import com.evergarden.cms.context.user.infrastructure.persistence.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CRUDProfileService {

    private ProfileRepository profileRepository;

    @Autowired
    public CRUDProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Mono<Profile> save(Profile profile) {
        return profileRepository.save(profile);
    }

    public Mono<Profile> findFirstByName(String name) {
        return profileRepository.findFirstByName(name)
            .switchIfEmpty(Mono.error(new RessourceNotFoundException("Profile " + name)));
    }

    public Mono<Profile> findById(String id) {
        return profileRepository.findById(id)
            .switchIfEmpty(Mono.error(new RessourceNotFoundException("Profile with id " + id)));
    }

    public Mono<Profile> findByNameOrId(String name, String id) {
        return findFirstByName(name).onErrorResume(throwable -> findById(id));
    }

    public Flux<Profile> showProfiles() {
        return profileRepository.findAll();
    }
}
