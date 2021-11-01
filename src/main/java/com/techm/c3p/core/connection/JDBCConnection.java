package com.techm.c3p.core.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * This class is built to support the JDBC connection (Legacy connection in C3P dao classes). Needs to reramp the JDBC connection with JPA.
 * @author AR115998
 *
 */
@Configuration
public class JDBCConnection {
	private static final Logger logger = LogManager.getLogger(JDBCConnection.class);
	@Value("${spring.datasource.driver-class-name}")
	private String driverClass;
	@Value("${spring.datasource.url}")
	private String datasourceUrl;
	@Value("${spring.datasource.username}")
	private String username;
	@Value("${spring.datasource.password}")
	private String password;

	private Connection createConnection() {
		Connection connection = null;
		try {
//			logger.info("datasourceUrl->"+datasourceUrl);
//			logger.info("driverClass->"+driverClass);
//			logger.info("username->"+username);
//			logger.info("password->"+password);
			Class.forName(driverClass);
			connection = DriverManager.getConnection(datasourceUrl, username, password);
		} catch (ClassNotFoundException exe) {
			logger.error("JDBCConnection ClassNotFoundException ->"+exe.getMessage());
		} catch (SQLException exe) {
			logger.error("JDBCConnection SQLException ->"+exe.getMessage());
		}
		return connection;
	}
	
	@Bean
	@Scope("prototype")
    public Connection getConnection() {
		//logger.info("getConnection datasourceUrl->"+datasourceUrl);
        return createConnection();
    }
}
