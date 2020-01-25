package com.evergarden.cms.context.user.domain.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Password {
    private String currentEncodedPassword;
    private String newPassword;
    private String confirmationPassword;

    private boolean isNewPasswordNotIdenticalWithExistingPassword() {
        return false;
    }

    private boolean isNewPasswordEqualsConfirmation() {
        return false;
    }

    public boolean isValid() {
        return false;
    }
}
