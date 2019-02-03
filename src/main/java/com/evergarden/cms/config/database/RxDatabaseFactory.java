package com.evergarden.cms.config.database;

import org.davidmoten.rx.jdbc.ConnectionProvider;
import org.davidmoten.rx.jdbc.Database;
import org.davidmoten.rx.jdbc.pool.NonBlockingConnectionPool;
import org.davidmoten.rx.jdbc.pool.Pools;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import java.sql.SQLException;

@Configuration
public class RxDatabaseFactory {

    @Bean
    @Scope("singleton")
    public Database getInstance(Logger logger, Environment env) throws SQLException {

        logger.debug("Starting non blocking connection with jdbc with max pool size of : "
            + (Runtime.getRuntime().availableProcessors() * 4));

        NonBlockingConnectionPool pool = Pools.nonBlocking()
            .maxPoolSize(Runtime.getRuntime().availableProcessors() * 4)
            .connectionProvider(ConnectionProvider.from(
                env.getProperty("spring.datasource.url"),
                env.getProperty("spring.datasource.username"),
                env.getProperty("spring.datasource.password")
            ))
            .build();

        return Database.from(pool);
    }
}
