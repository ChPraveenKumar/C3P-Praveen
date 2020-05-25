package com.techm.orion.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
 
public class ConnectionFactory {
    //static reference to itself
    private static ConnectionFactory instance = new ConnectionFactory();
    public static final String URL = "jdbc:mysql://localhost:3306/requestinfo?zeroDateTimeBehavior=convertToNull&rewriteBatchedStatements=true";
    public static final String USER = "root1234";
    public static final String PASSWORD = "root1234";
    public static final String DRIVER_CLASS = "com.mysql.jdbc.Driver"; 
    private static ConnectionFactory instance1 = new ConnectionFactory();
    //public static final String URL_TEMPLATE_DB = "jdbc:mysql://localhost:3306/Template_Schema?zeroDateTimeBehavior=convertToNull&rewriteBatchedStatements=true";

    //private constructor
    private ConnectionFactory() {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
     
    private Connection createConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("ERROR: Unable to Connect to Database.");
            System.out.println("Error code"+e.getErrorCode());
            System.out.println("Error Msg"+e.getMessage());
            System.out.println("Error Cause"+e.getCause());
        }
        return connection;
    }   
    /*private Connection createConnectionToTemplateDB() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL_TEMPLATE_DB, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("ERROR: Unable to Connect to Database.");
        }
        return connection;
    }   */
     
    public static Connection getConnection() {
        return instance.createConnection();
    }
   /* public static Connection getConnectionToTemplateDB() {
        return instance1.createConnectionToTemplateDB();
    } */
}