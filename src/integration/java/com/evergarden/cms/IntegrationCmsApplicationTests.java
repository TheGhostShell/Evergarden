package com.evergarden.cms;

import org.springframework.boot.test.context.SpringBootTest;


public class IntegrationCmsApplicationTests {
    private static MongoDbContainer mongo;

    static {
        IntegrationCmsApplicationTests.mongo = new MongoDbContainer("mongo:4.2.2")
            .withExposedPorts(27017);
        mongo.start();

        System.setProperty("spring.data.mongodb.database", "evergardenTest");
        System.setProperty("spring.data.mongodb.host", mongo.getContainerIpAddress());
        System.setProperty("spring.data.mongodb.port", mongo.getFirstMappedPort().toString());

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        IntegrationCmsApplicationTests.mongo.stop();
    }
}
