package com.hanami.cms.context.admin.application.jwt;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

@Component
public class EvergardenEncoder implements PasswordEncoder {
	
	private Environment env;
	
	private Logger logger;
	
	@Autowired
	public EvergardenEncoder(Environment env, Logger logger) {
		this.env = env;
		this.logger = logger;
	}
	
	@Override
	public String encode(CharSequence rawPassword) {
		
		String secret    = env.getProperty("encode.secret");
		int    iteration = Integer.parseInt(env.getProperty("encode.iteration"));
		int    keyLength = Integer.parseInt(env.getProperty("encode.keylength"));
		
		try {
			SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			PBEKeySpec spec = new PBEKeySpec(
				rawPassword.toString().toCharArray(),
				secret.getBytes(),
				iteration,
				keyLength
			);
			
			byte[] encoded = secretKeyFactory.generateSecret(spec).getEncoded();
			
			return Base64.getEncoder().encodeToString(encoded);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return false;
	}
}
