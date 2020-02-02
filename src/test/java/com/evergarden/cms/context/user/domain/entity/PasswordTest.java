package com.evergarden.cms.context.user.domain.entity;

import com.evergarden.cms.app.config.security.EvergardenEncoder;
import com.evergarden.cms.context.user.domain.exception.PasswordConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;

class PasswordTest {

    private EvergardenEncoder encoder;
    private String dbPassword;

    @BeforeEach
    public void setup() {
        Environment env = new MockEnvironment().withProperty("encode.secret", "3I|1@fAHd39$n)1_-?vk")
            .withProperty("encode.iteration", "32")
            .withProperty("encode.keylength", "256")
            .withProperty("encryption.algo", "SHA1PRNG")
            .withProperty("secure.random.algo", "PBKDF2WithHmacSHA512");

        EncodedCredential encodedCredential =
            new EncodedCredential("I3JycxGgTqJEQdD4VPeRcQ==", "F5w07xOZdOFNj3qqqikKP7Uhtj+b//O3dpWych1SbUg=");
        encoder = new EvergardenEncoder(env, encodedCredential);

        dbPassword = "F5w07xOZdOFNj3qqqikKP7Uhtj+b//O3dpWych1SbUg=";
    }

    @Test
    public void should_return_true_when_newPassword_equals_confirmation_password() {

        Password passwordSuccess = Password.builder()
            .currentPassword("pass")
            .dbPassword(dbPassword)
            .newPassword("newpass")
            .confirmPassword("newpass")
            .encoder(encoder)
            .build();

        Password passwordFailed = Password.builder()
            .currentPassword("pass")
            .dbPassword(dbPassword)
            .newPassword("newpass")
            .confirmPassword("badconfirmation")
            .encoder(encoder)
            .build();

        Assertions.assertTrue(passwordSuccess.isValid());
        Assertions.assertFalse(passwordFailed.isValid());
    }

    @Test
    public void should_return_false_when_newPassword_equals_current_password() {
        Password passwordSuccess = Password.builder()
            .currentPassword("pass")
            .dbPassword(dbPassword)
            .newPassword("newpass")
            .confirmPassword("newpass")
            .encoder(encoder)
            .build();

        Password passwordFailed = Password.builder()
            .currentPassword("pass")
            .dbPassword(dbPassword)
            .newPassword("pass")
            .confirmPassword("pass")
            .encoder(encoder)
            .build();

        Assertions.assertTrue(passwordSuccess.isValid());
        Assertions.assertFalse(passwordFailed.isValid());
    }

    @Test
    public void should_return_false_when_newPassword_notEquals_confirmation_password() {
        Password passwordSuccess = Password.builder()
            .currentPassword("pass")
            .dbPassword(dbPassword)
            .newPassword("newpass")
            .confirmPassword("newpass")
            .encoder(encoder)
            .build();

        Password passwordFailed = Password.builder()
            .currentPassword("pass")
            .dbPassword(dbPassword)
            .newPassword("Mypass")
            .confirmPassword("mypass")
            .encoder(encoder)
            .build();

        Assertions.assertTrue(passwordSuccess.isValid());
        Assertions.assertFalse(passwordFailed.isValid());
    }

    @Test
    public void should_return_false_when_newPassword_notEquals_db_password() {
        Password passwordSuccess = Password.builder()
            .currentPassword("pass")
            .dbPassword(dbPassword)
            .newPassword("newpass")
            .confirmPassword("newpass")
            .encoder(encoder)
            .build();

        Password passwordFailed = Password.builder()
            .currentPassword("passwordNotMatchWithDbPassword")
            .dbPassword(dbPassword)
            .newPassword("mypass")
            .confirmPassword("mypass")
            .encoder(encoder)
            .build();

        Assertions.assertTrue(passwordSuccess.isValid());
        Assertions.assertFalse(passwordFailed.isValid());
    }

    @Test
    public void should_return_newPassword_if_valid() {
        Password passwordSuccess = Password.builder()
            .currentPassword("pass")
            .dbPassword(dbPassword)
            .newPassword("newpass")
            .confirmPassword("newpass")
            .encoder(encoder)
            .build();

        Assertions.assertEquals("newpass", passwordSuccess.getNewPassword());
    }

    @Test
    public void should_throw_exception_if_password_constraint_is_violated() {
        Password passwordSuccess = Password.builder()
            .currentPassword("passs")
            .dbPassword(dbPassword)
            .newPassword("newpass")
            .confirmPassword("newpasss")
            .encoder(encoder)
            .build();

        Assertions.assertThrows(PasswordConstraintViolationException.class, passwordSuccess::getNewPassword);
    }
}