package com.techm.orion.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techm.orion.utility.TSALabels;

public class ConnectionFactory {
	private static final Logger logger = LogManager.getLogger(ConnectionFactory.class);
	private static ConnectionFactory instance = null;

	// private constructor
	private ConnectionFactory() {
		try {
			Class.forName(TSALabels.DRIVER_CLASS.getValue());
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
			connection = DriverManager.getConnection(TSALabels.URL_SQL.getValue(), TSALabels.USENAME_SQL.getValue(),
					TSALabels.PASSWORD_SQL.getValue());
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return connection;
	}

	private Connection createConnectionToTemplateDB() {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(TSALabels.URL_TEMPLATE_DB.getValue(),
					TSALabels.USENAME_SQL.getValue(), TSALabels.PASSWORD_SQL.getValue());
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return connection;
	}

	public static Connection getConnection() {
		return getInstance().createConnection();
	}

	public static Connection getConnectionToTemplateDB() {
		return getInstance().createConnectionToTemplateDB();
	}
}