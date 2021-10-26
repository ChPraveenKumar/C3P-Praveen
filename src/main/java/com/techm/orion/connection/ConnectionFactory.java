package com.techm.orion.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techm.orion.utility.C3PCoreAppLabels;

public class ConnectionFactory {
	private static final Logger logger = LogManager.getLogger(ConnectionFactory.class);
	private static ConnectionFactory instance = null;

	// private constructor
	private ConnectionFactory() {
		try {
			Class.forName(C3PCoreAppLabels.DRIVER_CLASS.getValue());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Single instance will be created for the ConnectionFactory
	 *
	 * @return instance
	 */
	public static ConnectionFactory getInstance() {
		if (instance == null) {
			instance = new ConnectionFactory();
		}
		return instance;
	}

	private Connection createConnection() {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(C3PCoreAppLabels.URL_SQL.getValue(), C3PCoreAppLabels.USENAME_SQL.getValue(),
					C3PCoreAppLabels.PASSWORD_SQL.getValue());
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return connection;
	}

	public static Connection getConnection() {
		return getInstance().createConnection();
	}
}
