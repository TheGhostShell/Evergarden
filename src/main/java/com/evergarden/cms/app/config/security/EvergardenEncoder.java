package com.evergarden.cms.app.config.security;

import com.evergarden.cms.context.user.domain.entity.EncodedCredential;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
@Slf4j
public class EvergardenEncoder implements PasswordEncoder {

  private Environment env;
  private EncodedCredential encodedCredential;

  @Autowired
  public EvergardenEncoder(Environment env) {
    this.env = env;
  }

  public EvergardenEncoder(@NotNull Environment env, EncodedCredential encodedCredential) {
    this.env = env;
    this.encodedCredential = encodedCredential;
  }

  @Override
  public String encode(CharSequence rawPassword) {

    byte[] salt;
    String staticSalt = env.getProperty("encode.secret");
    int iteration = Integer.parseInt(env.getProperty("encode.iteration"));
    int keyLength = Integer.parseInt(env.getProperty("encode.keylength"));

    try {
      salt = getSaltByte();
    } catch (Exception e) {
      log.error(e.getMessage());
      salt = staticSalt.getBytes();
    }

    try {
      SecretKeyFactory secretKeyFactory =
          SecretKeyFactory.getInstance(env.getProperty("encryption.algo"));

      PBEKeySpec spec =
          new PBEKeySpec(rawPassword.toString().toCharArray(), salt, iteration, keyLength);

      byte[] encoded = secretKeyFactory.generateSecret(spec).getEncoded();

      encodedCredential =
          new EncodedCredential(convToString(salt), Base64.getEncoder().encodeToString(encoded));

      log.debug("Generated salt : " + convToString(salt));

      return Base64.getEncoder().encodeToString(encoded);

    } catch (Exception e) {
      log.error(e.getMessage());
      return null;
    }
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {

    try {
      int iteration = Integer.parseInt(env.getProperty("encode.iteration"));
      int keyLength = Integer.parseInt(env.getProperty("encode.keylength"));
      byte[] saltByte = Base64.getDecoder().decode(encodedCredential.getSalt());
      SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");

      PBEKeySpec spec =
          new PBEKeySpec(rawPassword.toString().toCharArray(), saltByte, iteration, keyLength);

      byte[] encoded = secretKeyFactory.generateSecret(spec).getEncoded();

      String expectedEncodedPass = Base64.getEncoder().encodeToString(encoded);

      return expectedEncodedPass.equals(encodedPassword);

    } catch (Exception e) {
      return false;
    }
  }

  private byte[] getSaltByte() throws NoSuchAlgorithmException {
    SecureRandom secure = SecureRandom.getInstance(env.getProperty("secure.random.algo"));

    byte[] salt = new byte[16];

    secure.nextBytes(salt);

    return salt;
  }

  private String convToString(byte[] salt) {
    return Base64.getEncoder().encodeToString(salt);
  }

  public EncodedCredential getEncodedCredential() {
    return encodedCredential;
  }
}
