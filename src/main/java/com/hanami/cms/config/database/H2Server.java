package com.hanami.cms.config.database;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.SQLException;

@Configuration
public class H2Server {
	
	@Bean(initMethod = "start", destroyMethod = "stop")
	public Server h2ServerConfiguration(Logger logger) throws SQLException {
		
		logger.debug("Starting h2 server database");
		
		return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
	}
}
