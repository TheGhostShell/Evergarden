package com.evergarden.cms.context.user.application.service;

import com.evergarden.cms.app.config.security.EvergardenEncoder;
import com.evergarden.cms.context.user.domain.entity.EncodedCredential;
import com.evergarden.cms.context.user.domain.entity.Password;
import com.evergarden.cms.context.user.domain.exception.PasswordConstraintViolationException;
import com.evergarden.cms.context.user.infrastructure.controller.input.PasswordRequest;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UpdatePasswordService {
    private CRUDUserService crudUserService;
    private Environment env;

    public UpdatePasswordService(CRUDUserService crudUserService, Environment env) {
        this.crudUserService = crudUserService;
        this.env = env; // TODO avoid used Environment instance prefer @Value annotation with default value
    }

    public Mono<Boolean> updatePassword(PasswordRequest passwordRequest, String userId) {

       return crudUserService.findUser(userId)
            .flatMap(user -> {

                EncodedCredential encodedCredential = new EncodedCredential(user.getSalt(), user.getPassword());
                EvergardenEncoder encoder = new EvergardenEncoder(env, encodedCredential);

                Password password = Password.builder()
                    .newPassword(passwordRequest.getNewPassword())
                    .confirmPassword(passwordRequest.getConfirmationPassword())
                    .dbPassword(user.getPassword())
                    .currentPassword(passwordRequest.getCurrentPassword())
                    .encoder(encoder)
                    .build();

                if (password.isValid()) {
                    encoder.encode(password.getNewPassword());
                    user.setEncodedCredential(encoder.getEncodedCredential());
                    return Mono.just(user);
                }

                return Mono.error(new PasswordConstraintViolationException());
            })
           .flatMap(user -> crudUserService.updateOrSave(user).map(user1 -> true));
    }
}
