package com.evergarden.cms.app.plugin;

import org.apache.felix.framework.Felix;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Configuration
public class OsgiLoader {

    private Map<String, String> configMap = new HashMap<>();

    @Bean
    public BundleContext loadPlugin(Logger logger) {
        configMap.put(Constants.FRAMEWORK_STORAGE_CLEAN, "onFirstInit");
        Felix framework = new Felix(configMap);
        try {
            framework.init();

            logger.info("Starting good");

            Pattern pattern = Pattern.compile("^*\\.jar$");

            try (Stream<Path> paths = Files.walk(Paths.get("plugins/"))) {
                paths
                    .filter(Files::isRegularFile)
                    .peek(path -> {
                        if (pattern.matcher(path.toString()).find()) {
                            logger.info("FOUND AND MATCH !! " + path.toString());
                        }
                    })
                    .count();
            }

            BundleContext context = framework.getBundleContext();
            Bundle        plugin  = context.installBundle("file:plugins/test-bnd-plug-1.0-SNAPSHOT.jar");
            framework.start();
            plugin.start();
            URL pathurl = plugin.getResource("evergarden.properties");
            logger.info(pathurl.getFile());
            framework.stop();

            return context;
        } catch (BundleException exception) {
            //logger.error(exception.getMessage());
            exception.printStackTrace();
        } catch (Exception e) {
            //logger.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
