package com.hanami.cms.config.database;

import org.davidmoten.rx.jdbc.ConnectionProvider;
import org.davidmoten.rx.jdbc.Database;
import org.davidmoten.rx.jdbc.pool.NonBlockingConnectionPool;
import org.davidmoten.rx.jdbc.pool.Pools;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

@Configuration
public class RxDatabaseFactory {
	
	@Bean
	public Database getInstance(Logger logger) throws SQLException {
		
		logger.info("Starting non blocking connection with jdbc with max pool size is: " + (Runtime.getRuntime().availableProcessors() * 4));
		
		NonBlockingConnectionPool pool = Pools.nonBlocking()
			.maxPoolSize(Runtime.getRuntime().availableProcessors() * 4)
			.connectionProvider(ConnectionProvider.from("jdbc:h2:file:/home/john/Documents/project/jvm/hanami-cms/h2", "user", "user"))
			.build();
		
		return Database.from(pool);
	}
}
