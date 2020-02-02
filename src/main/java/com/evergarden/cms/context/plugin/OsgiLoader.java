package com.evergarden.cms.context.plugin;


import com.evergarden.sdk.database.Migration;
import org.apache.felix.framework.Felix;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Configuration
public class OsgiLoader {

    private Map configMap = new HashMap<>();

    private String migration;

    //@Bean
    public BundleContext loadPlugin(Logger logger) {
        configMap.put(Constants.FRAMEWORK_STORAGE_CLEAN, "onFirstInit");
        configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, "com.evergarden.sdk, com.evergarden.sdk.database");
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
            //context.registerService(DSLContext.class, jooquer.getJooqInstance(), null);
            //Bundle        sdkb  = context.installBundle("file:plugins/sdk-0.0.1-SNAPSHOT.jar");
            Bundle        plugin  = context.installBundle("file:plugins/test-bnd-plug-1.0-SNAPSHOT.jar");
            //Bundle        loader  = context.installBundle("file:plugins/OSGiLoaderPlugin-1.0-SNAPSHOT.jar");
            framework.start();
            //sdkb.start();
            plugin.start();
            //loader.start();
            URL pathUrl = plugin.getResource("evergarden.properties");
            read(pathUrl);

            ServiceReference reference= plugin.getBundleContext().getServiceReference(Migration.class);
            Migration sr = (Migration) context.getService(reference);
            sr.execute("hello from cast migration framework");
            //sr.execute(jooquer.getJooqInstance());
//            Class<?> migrationClass = plugin.loadClass(migration);
//            System.out.println( (migrationClass.newInstance()).getClass());
            //Object migrationObject = BeanUtils.instantiateClass(migrationClass);
            //mig.execute(jooquer.getJooqInstance());
            logger.info("the path url is " + pathUrl.getFile());
            //framework.stop();

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

    private void read(URL path) {
        Properties  prop  = new Properties();
        InputStream input = null;
        try {
            input = path.openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            // load a properties file
            prop.load(input);

            // get the property value and print it out
            migration = prop.getProperty("migration");
            System.out.println(prop.getProperty("migration"));
            System.out.println(prop.getProperty("type"));

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
