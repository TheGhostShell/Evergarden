package com.evergarden.cms.context.admin.domain.security;

import com.evergarden.cms.context.admin.domain.entity.EncodedCredential;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.core.env.Environment;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest
class EvergardenEncoderTest {
	
	@Autowired
	Environment env;
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/*
	SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			
			PBEKeySpec spec = new PBEKeySpec(
				rawPassword.toString().toCharArray(),
				salt,
				iteration,
				keyLength
			);
			
			byte[] encoded = secretKeyFactory.generateSecret(spec).getEncoded();
			
			encodedCredentials = new EncodedCredential(convToString(salt), Base64.getEncoder().encodeToString(encoded));
			
			logger.debug("Generated salt : " + convToString(salt));
			
			return Base64.getEncoder().encodeToString(encoded);*
	 */
	/*
	private byte[] getSaltByte() throws NoSuchAlgorithmException {
		
		SecureRandom secure = SecureRandom.getInstance("SHA1PRNG");
		
		byte[] salt = new byte[16];
		
		secure.nextBytes(salt);
		
		return salt;
	}
	 */
	/*
	private String convToString(byte[] salt) {
		
		return Base64.getEncoder().encodeToString(salt);
	}
	 */
	
	@BeforeEach
	void setUp() {
	
	}
	
	@Test
	void encode() {
		String            password = "weak_password";
		EvergardenEncoder encoder  = new EvergardenEncoder(env, logger);
		encoder.encode(password);
		EncodedCredential encodedCredential = encoder.getEncodedCredentials();
		
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
	void matches() {
	}
	
	@Test
	void getEncodedCredentials() {
	}
}