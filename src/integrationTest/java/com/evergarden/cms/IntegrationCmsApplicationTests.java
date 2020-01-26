package com.evergarden.cms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegrationCmsApplicationTests {
    public static MongoDbContainer mongo;
    private static Logger logger;

    static {
        mongo = new MongoDbContainer("mongo:3.2").withExposedPorts(27017);
        logger = LoggerFactory.getLogger(IntegrationCmsApplicationTests.class);
        mongo.start();
        String dbname = "evergardenTest";


        System.setProperty("spring.data.mongodb.database", dbname);
        System.setProperty("spring.data.mongodb.host", mongo.getContainerIpAddress());
        System.setProperty("spring.data.mongodb.port", mongo.getFirstMappedPort().toString());

        System.out.printf(
            "Starting mongodb for integration test on host %s:%s and db name %s  \n",
            mongo.getContainerIpAddress(),
            mongo.getFirstMappedPort().toString(),
            dbname
        );

    }
}
