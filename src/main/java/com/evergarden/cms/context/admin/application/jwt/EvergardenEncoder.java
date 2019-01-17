package com.evergarden.cms.context.admin.application.jwt;

import com.evergarden.cms.context.admin.domain.entity.EncodedCredential;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class EvergardenEncoder implements PasswordEncoder {
	
	private Environment env;
	
	private Logger logger;
	
	private EncodedCredential encodedCredentials;
	
	@Autowired
	public EvergardenEncoder(Environment env, Logger logger) {
		this.env = env;
		this.logger = logger;
	}
	
	@Override
	public String encode(CharSequence rawPassword) {
		
		byte[] salt;
		String staticSalt = env.getProperty("encode.secret");
		int    iteration  = Integer.parseInt(env.getProperty("encode.iteration"));
		int    keyLength  = Integer.parseInt(env.getProperty("encode.keylength"));
		
		try {
			salt = getSaltByte();
		} catch (Exception e) {
			salt = staticSalt.getBytes();
		}
		
		try {
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
			
			return Base64.getEncoder().encodeToString(encoded);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return false;
	}
	
	private byte[] getSaltByte() throws NoSuchAlgorithmException {
		
		SecureRandom secure = SecureRandom.getInstance("SHA1PRNG");
		
		byte[] salt = new byte[16];
		
		secure.nextBytes(salt);
		
		return salt;
	}
	
	private String convToString(byte[] salt) {
		
		return Base64.getEncoder().encodeToString(salt);
	}
	
	public EncodedCredential getEncodedCredentials() {
		return encodedCredentials;
	}
}
