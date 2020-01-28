package com.evergarden.cms;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class IntegrationCmsApplicationTests {
    public static MongoDbContainer mongo;

    static {
        mongo = new MongoDbContainer("mongo:3.2").withExposedPorts(27017);
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
