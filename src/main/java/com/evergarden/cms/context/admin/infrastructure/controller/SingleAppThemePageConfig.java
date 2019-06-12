package com.evergarden.cms.context.admin.infrastructure.controller;

import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.resource.PathResourceResolver;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.evergarden.cms.context.admin.infrastructure.controller.FrontControllerHandler.FRONT_CONTROLLER;
import static com.evergarden.cms.context.admin.infrastructure.controller.FrontControllerHandler.URL_SEPARATOR;

@Configuration
public class SingleAppThemePageConfig implements WebFluxConfigurer {
    public static final String IGNORED_PATH = "/v1";
    private static final String PATH_PATTERNS = "/home/**";

    private final FrontControllerHandler frontControllerHandler;
    private final ApplicationContext applicationContext;
    private final List<String> staticLocations = new ArrayList();

    public SingleAppThemePageConfig(
            ResourceProperties resourceProperties,
            FrontControllerHandler frontControllerHandler,
            ApplicationContext applicationContext
    ) {
        this.frontControllerHandler = frontControllerHandler;
        this.applicationContext = applicationContext;
        // this.staticLocations = resourceProperties.getStaticLocations();
        this.staticLocations.add("classpath:/public/theme/");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(PATH_PATTERNS)
                .addResourceLocations(staticLocations.toArray(new String[0]))
                .resourceChain(true)
                .addResolver(new SinglePageAppResourceResolver());
    }

    private class SinglePageAppResourceResolver extends PathResourceResolver {

        private final Resource frontControllerResource;

        SinglePageAppResourceResolver() {
            this.frontControllerResource = Arrays
                    .stream(staticLocations.toArray())
                    .peek(s -> {
                        System.out.println(s);
                    })
                    .map(path -> applicationContext.getResource(path + FRONT_CONTROLLER))
                    .filter(this::resourceExistsAndIsReadable)
                    .findFirst()
                    .map(frontControllerHandler::buildFrontControllerResource)
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
                resource = location.createRelative(resourcePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (resourceExistsAndIsReadable(resource)) {
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
                if (resourceExistsAndIsReadable(location.createRelative(FRONT_CONTROLLER))) {
                    return Mono.just(frontControllerResource);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return Mono.empty();
        }

        private boolean resourceExistsAndIsReadable(Resource resource) {
            Objects.requireNonNull(resource, "resource cannot be null");
            return resource.exists() && resource.isReadable();
        }
    }
}
