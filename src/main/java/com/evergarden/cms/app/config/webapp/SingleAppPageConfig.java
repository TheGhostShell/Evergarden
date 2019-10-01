package com.evergarden.cms.app.config.webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.resource.PathResourceResolver;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

@Configuration
public class SingleAppPageConfig implements WebFluxConfigurer {
    public static final String IGNORED_PATH = "/v1";
    private static final String PATH_PATTERNS = "/admin/**";
    private static final String FRONT_CONTROLLER = "index.html";
    private static final String URL_SEPARATOR = "/";

    private final ApplicationContext applicationContext;
//    private final String[] staticLocations;

    @Autowired
    public SingleAppPageConfig(
            ResourceProperties resourceProperties,
            ApplicationContext applicationContext
    ) {
        this.applicationContext = applicationContext;
//        this.staticLocations = resourceProperties.getStaticLocations();
//        this.staticLocations[staticLocations.length - 1] = "classpath:/public/admin/";
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String[] adminLoc = {"classpath:public/admin/"};

        String[] themeLoc = {"file:./template/theme/", "classpath:/public/admin/"};

        registry.addResourceHandler("/**")
                .addResourceLocations(themeLoc)
                .resourceChain(true)
                .addResolver(new SinglePageAppResourceResolver(themeLoc));

        registry.addResourceHandler("/admin/**")
                .addResourceLocations(adminLoc)
                .resourceChain(true)
                .addResolver(new SinglePageAppResourceResolver(adminLoc));
    }

    private class SinglePageAppResourceResolver extends PathResourceResolver {

        private final Resource frontControllerResource;

        SinglePageAppResourceResolver(String[] staticLocations) {
            this.frontControllerResource = Arrays
                    .stream(staticLocations)
                    .peek(s -> {
                        System.err.println("the static location " + s);
                    })
                    .map(path -> applicationContext.getResource(path + FRONT_CONTROLLER))
                    .filter(this::resourceExistsAndIsReadable)
                    .findFirst()
                    .orElseGet(() -> {
                        System.out.println(FRONT_CONTROLLER + " not found. "
                                + "Ensure you have built the frontend part if you are not in dev mode.");

                        return null;
                    });
        }

        @Override
        protected Mono<Resource> getResource(String resourcePath, Resource location) {
            Resource resource = null;
            try {
                System.err.println("Oveerride get ressource path resolver " + resourcePath + " + " + location.toString() + " + " +location.exists());
                resource = location.createRelative(resourcePath);
                System.err.println("new resources " + resource.toString());
            } catch (IOException e) {
                //e.printStackTrace();
            }
            if (resourceExistsAndIsReadable(resource)) {
                System.out.println("Resource exist and is readable");
                //if the asked resource is index.html itself, we serve it with the base-href rewritten
                if (resourcePath.endsWith(FRONT_CONTROLLER)) {
                    return Mono.just(frontControllerResource);
                }
                //here we serve js, css, etc.
                return Mono.just(resource);
            }

            //do not serve a Resource on an ignored path
            if ((URL_SEPARATOR + resourcePath).startsWith(IGNORED_PATH)) {
                return Mono.empty();
            }

            //we are in the case of an angular route here, we rewrite to index.html
            try {
                if(Pattern.matches("^.*\\..*", resourcePath)){
                    System.out.println("pattern matching ");
                    return Mono.empty();
                }
                if (resourceExistsAndIsReadable(location.createRelative(FRONT_CONTROLLER))) {
                    System.out.println("another resource is readable");
                    return Mono.just(frontControllerResource);
                }
            } catch (IOException e) {
                //e.printStackTrace();
            }

            return Mono.empty();
        }

        private boolean resourceExistsAndIsReadable(Resource resource) {
            Objects.requireNonNull(resource, "resource cannot be null");
            if(resource.exists() && resource.isReadable()){
                System.out.println(resource.toString());
            }
            return resource.exists() && resource.isReadable();
        }
    }
}
