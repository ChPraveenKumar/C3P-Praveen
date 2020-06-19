package com.techm.orion.connection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {
	// static reference to itself
	private static ConnectionFactory instance = null;
	public static String TSA_PROPERTIES_FILE = "TSA.properties";
	public static final Properties TSA_PROPERTIES = new Properties();
	public static final String USER = "root";
	public static final String PASSWORD = "root";
	public static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";

	// private constructor
	private ConnectionFactory() {
		try {
			Class.forName(DRIVER_CLASS);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Single instance will be created for the CommonUtil
	 *
	 * @return instance
	 * @throws IOException
	 */
	public static ConnectionFactory getInstance() {
		if (instance == null) {
			loadProperties();
			instance = new ConnectionFactory();
		}
		return instance;
	}

	private Connection createConnection() {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(
					TSA_PROPERTIES.getProperty("serverDBURL"), USER, PASSWORD);
		} catch (SQLException e) {
			System.out.println("ERROR: Unable to Connect to Database.");
			System.out.println("Error code" + e.getErrorCode());
			System.out.println("Error Msg" + e.getMessage());
			System.out.println("Error Cause" + e.getCause());
		}
		return connection;
	}

	private Connection createConnectionToTemplateDB() {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(
					TSA_PROPERTIES.getProperty("templateDBURL"), USER,
					PASSWORD);
		} catch (SQLException e) {
			System.out.println("ERROR: Unable to Connect to Database.");
		}
		return connection;
	}

	public static Connection getConnection() {
		return getInstance().createConnection();
	}

	public static Connection getConnectionToTemplateDB() {
		return getInstance().createConnectionToTemplateDB();
	}

	public static boolean loadProperties() {
		InputStream tsaPropFile = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(TSA_PROPERTIES_FILE);

		try {
			TSA_PROPERTIES.load(tsaPropFile);
		} catch (IOException exc) {
			exc.printStackTrace();
			return false;
		}
		return false;
	}
}