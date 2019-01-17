package com.evergarden.cms.app.router;

//@Configuration
public class RouteLoader {
	
	//@Bean
	//public RouterFunction loadRoute() throws NoSuchMethodException, NoSuchAlgorithmException, IllegalAccessException, InvocationTargetException {
//		RouteBuilder builder = new RouteBuilder();
//
//		Handler handler = (Request request)->{
//			return new Response(
//				new Header(),
//				new Body("personal data"),
//				new StatusCode()
//			);
//		};
//
//		Route route = new Route("/tibou", HttpMethod.get, handler);
//
//		builder.add(route.getMethod(), route.getPath(), HandlerConverter.toHandlerFunction(route))
//			.add(HttpMethod.get, "/test", (ServerRequest request)->{
//				return ServerResponse.ok().body(BodyInserters.fromObject("ceci est un test"));
//			});
//
//		builder.add(HttpMethod.get, "/working", (ServerRequest request)->{
//			return ServerResponse.ok().body(BodyInserters.fromObject("success magistral"));
//		});
//
//		return builder.build();
	//}
}
