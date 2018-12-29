package com.hanami.cms.context.admin.application.config;

import com.hanami.cms.context.admin.application.basic.BasicAuthenticationSuccessHandler;
import com.hanami.cms.context.admin.application.bearer.EvergardenAuthenticationManager;
import com.hanami.cms.context.admin.application.bearer.SecurityContextRepository;
import com.hanami.cms.context.admin.application.bearer.ServerHttpBearerAuthenticationConverter;
import com.hanami.cms.context.admin.infrastructure.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
public class SecurityConfig {
	
	private UserRepository userRepository;
	
	private EvergardenAuthenticationManager authenticationManager;
	
	private SecurityContextRepository securityContextRepository;
	
	@Autowired
	public SecurityConfig(UserRepository userRepository, EvergardenAuthenticationManager authenticationManager, SecurityContextRepository securityContextRepository) {
		this.userRepository = userRepository;
		this.authenticationManager = authenticationManager;
		this.securityContextRepository = securityContextRepository;
	}
	
	/**
	 * this bean is for create a fake user to test our security process login =john and password = pass
	 *
	 * @return MapReactiveUserDetailsService
	 */
	@Bean
	public MapReactiveUserDetailsService userRepositoryFake() {
		UserDetails user = User
			.withDefaultPasswordEncoder()
			.username("john")
			.password("pass")
			.roles("USER", "ADMIN", "GUEST")
			.build();
		
		return new MapReactiveUserDetailsService(user);
	}
	
	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		/*http
			.authorizeExchange()
			.anyExchange()// disable authorization process
			.authenticated()// activate authentication process
			.and().httpBasic();// specify the authentication type strategy*/
		
		http
			.authorizeExchange().pathMatchers("/v1/private/**").hasRole("ADMIN")
			//.and().authorizeExchange().pathMatchers("/v1/private/**").authenticated()
			.and().authorizeExchange().pathMatchers("/v1/guest").permitAll()
			.and().authorizeExchange().pathMatchers("/v1/**").hasRole("GUEST")
			.and().authenticationManager(authenticationManager)
			.securityContextRepository(securityContextRepository)
			.csrf().disable()
			.authorizeExchange()
			.pathMatchers("/**")
			.permitAll();
		
		/*http.csrf().disable()
			.formLogin().disable()
			.httpBasic().disable()
			.authenticationManager(authenticationManager)
			.securityContextRepository(securityContextRepository)
			.authorizeExchange()
			.pathMatchers(HttpMethod.OPTIONS).permitAll()
			.pathMatchers("/login").permitAll()
			.anyExchange().authenticated()
			.and().build();*/
		
		return http.build();
	}
	
	/**
	 * Use the already implemented logic in  AuthenticationWebFilter and set a custom
	 * SuccessHandler that will return a JWT when a user is authenticated with user/password
	 * Create an AuthenticationManager using the UserDetailsService defined above
	 *
	 * @return AuthenticationWebFilter
	 */
	private AuthenticationWebFilter basicAuthenticationFilter() {
		UserDetailsRepositoryReactiveAuthenticationManager authManager =
			new UserDetailsRepositoryReactiveAuthenticationManager(userRepositoryFake());
		ServerAuthenticationSuccessHandler successHandler = new BasicAuthenticationSuccessHandler();
		
		AuthenticationWebFilter basicAuthenticationFilter = new AuthenticationWebFilter(authManager);
		basicAuthenticationFilter.setAuthenticationSuccessHandler(successHandler);
		
		return basicAuthenticationFilter;
	}
	
	/**
	 * Use the already implemented logic by AuthenticationWebFilter and set a custom
	 * converter that will handle requests containing a Bearer token inside
	 * the HTTP Authorization header.
	 * Set a dummy authentication manager to this filter, it's not needed because
	 * the converter handles this.
	 *
	 * @return bearerAuthenticationFilter that will authorize requests containing a JWT
	 */
	/*private AuthenticationWebFilter bearerAuthenticationFilter() {
		ReactiveAuthenticationManager authManager = new EvergardenAuthenticationManager();
		AuthenticationWebFilter bearerAuthenticationFilter =
			new AuthenticationWebFilter(authManager);
		Function<ServerWebExchange, Mono<Authentication>> bearerConverter =
			new ServerHttpBearerAuthenticationConverter();
		
		bearerAuthenticationFilter.setAuthenticationConverter(bearerConverter);
		bearerAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/v1/**"));
		
		return bearerAuthenticationFilter;
	}*/
}
