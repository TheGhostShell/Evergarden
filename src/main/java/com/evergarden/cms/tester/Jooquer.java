package com.evergarden.cms.tester;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component
public class Jooquer {

    private Environment env;
    private Logger logger;

    @Autowired
    public Jooquer(Environment env, Logger logger) {
        this.env = env;
        this.logger = logger;
    }


    public DSLContext getJooqInstance() {
        try {
            Connection connection = DriverManager.getConnection(
                env.getProperty("spring.datasource.url"),
                env.getProperty("spring.datasource.username"),
                env.getProperty("spring.datasource.password")
            );
            DSLContext jooq = DSL.using(connection, SQLDialect.H2);
            logger.info("creating jooq instance");
            return  jooq;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
