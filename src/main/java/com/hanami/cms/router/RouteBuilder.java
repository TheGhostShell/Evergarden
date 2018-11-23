package com.hanami.cms.router;

import com.hanami.sdk.router.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;

public class RouteBuilder {
	
	ArrayList<RouteAgregate> routeAggregator = new ArrayList<>();
	
	private String hash;
	
	public RouteBuilder add(HttpMethod method, String path, HandlerFunction<ServerResponse> handler) {
		
		routeAggregator.add(new RouteAgregate(method, path, handler));
		
		return this;
	}
	
	public RouterFunction<ServerResponse> build() throws NoSuchAlgorithmException, NoSuchMethodException,
		InvocationTargetException,
		IllegalAccessException {
		
		String hashed = hash("flocondria" + Long.toString(Instant.now().getEpochSecond()));
		
		Logger logger = LoggerFactory.getLogger(RouteBuilder.class);
		
		logger.info("The hash is " + hashed);
		
		RouterFunction routes = RouterFunctions.route(RequestPredicates.GET("/" + hashed), serverRequest -> {
			return ServerResponse.ok().body(BodyInserters.fromObject("Hello hash is : " + hashed));
		});
		
		for (RouteAgregate route : routeAggregator) {
			
			HttpMethod      key     = route.getMethod();
			HandlerFunction handler = route.getHandler();
			String          path    = route.getPath();
			
			Method method = RequestPredicates.class.getMethod(key.getVerb(), String.class);
			Object obj = new RequestPredicates() {
			};
			
			routes = RouterFunctions.route((RequestPredicate) method.invoke(obj, path), handler)
				.and(routes);
		}
		
		return routes;
	}
	
	/**
	 * @param hash a simple string to hash
	 * @return String
	 */
	private String hash(String hash) throws NoSuchAlgorithmException {
		
		MessageDigest md          = MessageDigest.getInstance("MD5");
		byte[]        hashInBytes = md.digest(hash.getBytes(StandardCharsets.UTF_8));
		StringBuilder sb          = new StringBuilder();
		
		for (byte b : hashInBytes) {
			sb.append(String.format("%02x", b));
		}
		
		this.hash = sb.toString();
		
		return this.hash;
	}
	
	public String getHash() {
		return hash;
	}
}
