package com.evergarden.cms.context.user.domain.entity;

import com.evergarden.cms.app.config.security.EvergardenEncoder;
import com.evergarden.cms.context.user.domain.exception.PasswordConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@AllArgsConstructor
@Slf4j
public class Password {

    private String            dbPassword;
    private String            currentPassword;
    private String            newPassword;
    private String            confirmPassword;
    private EvergardenEncoder encoder;

    private boolean isNewPasswordNotIdenticalWithCurrentPassword() {
        log.debug("isNewPasswordNotIdenticalWithCurrentPassword {} ", !currentPassword.equals(newPassword));
        return !currentPassword.equals(newPassword);
    }

    private boolean isNewPasswordNotIdenticalWithDbPassword() {
        log.debug("isNewPasswordNotIdenticalWithDbPassword {} ", !encoder.matches(newPassword, dbPassword));
        return !encoder.matches(newPassword, dbPassword);
    }

    private boolean isCurrentPasswordMatchesDbPassword() {
        log.debug("isCurrentPasswordMatchesDbPassword {} ", encoder.matches(currentPassword, dbPassword));
        return encoder.matches(currentPassword, dbPassword);
    }

    private boolean isNewPasswordEqualsConfirmation() {
        log.debug("isNewPasswordEqualsConfirmation {} ", newPassword.equals(confirmPassword));
        return newPassword.equals(confirmPassword);
    }

    public boolean isValid() {
        return isNewPasswordEqualsConfirmation() && isNewPasswordNotIdenticalWithCurrentPassword() &&
            isNewPasswordNotIdenticalWithDbPassword() && isCurrentPasswordMatchesDbPassword();
    }

    public String getNewPassword() {
        if (isValid()) {
            return newPassword;
        }

        throw new PasswordConstraintViolationException();
    }
}
