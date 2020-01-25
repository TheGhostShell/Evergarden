package com.evergarden.cms.context.user.application.security;

import com.evergarden.cms.app.config.security.EvergardenEncoder;
import com.evergarden.cms.context.user.domain.entity.EncodedCredential;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes={ReactiveMongoRepository.class})
class EvergardenEncoderTest {

    @Autowired
    Environment env;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private EvergardenEncoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new EvergardenEncoder(env, logger);
    }

    @Test
    void encode() {
        String password = "weak_password";
        encoder.encode(password);
        EncodedCredential encodedCredential = encoder.getEncodedCredential();

        byte[] saltByte = Base64.getDecoder().decode(encodedCredential.getSalt());

        try {

            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");

            PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                saltByte,
                Integer.parseInt(env.getProperty("encode.iteration")),
                Integer.parseInt(env.getProperty("encode.keylength"))
            );

            byte[] encoded = secretKeyFactory.generateSecret(spec).getEncoded();

            String expectedPassEncoded = Base64.getEncoder().encodeToString(encoded);

            assertEquals(expectedPassEncoded, encodedCredential.getEncodedPassword());

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    @Test
    void encodeFailWithBadAlgorithm() {
        Environment envMock = mock(Environment.class);
        encoder = new EvergardenEncoder(envMock, logger);

        when(envMock.getProperty("secure.ramdom.algo")).thenReturn("bad-algorithm");
        when(envMock.getProperty("encode.iteration")).thenReturn(env.getProperty("encode.iteration"));
        when(envMock.getProperty("encode.keylength")).thenReturn(env.getProperty("encode.keylength"));
        when(envMock.getProperty("encode.secret")).thenReturn(env.getProperty("encode.secret"));

        int              iteration        = Integer.parseInt(env.getProperty("encode.iteration"));
        int              keyLength        = Integer.parseInt(env.getProperty("encode.keylength"));
        byte[]           saltByte         = env.getProperty("encode.secret").getBytes();
        SecretKeyFactory secretKeyFactory = null;

        try {
            secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        } catch (NoSuchAlgorithmException e) {}

        PBEKeySpec spec = new PBEKeySpec(
            "weak_password".toCharArray(),
            saltByte,
            iteration,
            keyLength
        );

        byte[] encoded = new byte[0];
        try {
            encoded = secretKeyFactory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        String expectedEncodedPass = Base64.getEncoder().encodeToString(encoded);

        String encPass = encoder.encode("weak_password");

        assertNull(encPass);

        when(envMock.getProperty("encryption.algo")).thenReturn(env.getProperty("encryption.algo"));

        encPass = encoder.encode("weak_password");

        assertEquals(expectedEncodedPass, encPass);
    }

    @Test
    void matches() {
        String password = "weak_password";
        encoder.encode(password);
        EncodedCredential encodedCredential = encoder.getEncodedCredential();
        EvergardenEncoder eveEncoder        = new EvergardenEncoder(env, logger, encodedCredential);

        assertTrue(eveEncoder.matches(password, encodedCredential.getEncodedPassword()));
    }

    @Test
    void matchesFail() {
        String password = "weak_password";
        encoder.encode(password);
        EncodedCredential encodedCredentialMock = mock(EncodedCredential.class);
        EvergardenEncoder eveEncoder            = new EvergardenEncoder(env, logger, encodedCredentialMock);

        when(encodedCredentialMock.getSalt()).thenAnswer(invocation -> {
            throw new Exception();
        });

        assertFalse(eveEncoder.matches(password, encodedCredentialMock.getEncodedPassword()));
    }
}