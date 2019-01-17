package com.evergarden.cms.context.admin.application.basic;

import com.evergarden.cms.context.admin.application.jwt.JWTTokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class BasicAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
	
	/**
	 * A successful authentication object us used to create a JWT object and
	 * added in the authorization header of the current WebExchange
	 *
	 * @param webFilterExchange
	 * @param authentication
	 * @return Mono<Void>
	 */
	@Override
	public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
		
		ServerWebExchange exchange = webFilterExchange.getExchange();
		
		//TODO refactor this nasty implementation
		exchange.getResponse()
			.getHeaders()
			.add(HttpHeaders.AUTHORIZATION, getHttpAuthHeaderValue(authentication)); // si tout ce passe après
		// l'autentication on ajout dans le header le token
		
		return Mono.empty();
		// useless
		//return webFilterExchange.getChain().filter(exchange);
	}
	
	private static String getHttpAuthHeaderValue(Authentication authentication) {
		return String.join(" ", "Bearer", tokenFromAuthentication(authentication));
	}
	
	private static String tokenFromAuthentication(Authentication authentication) {
		
		return JWTTokenService.generateToken(
			authentication.getName(),
			authentication.getCredentials(),
			authentication.getAuthorities()
		);
	}
}
