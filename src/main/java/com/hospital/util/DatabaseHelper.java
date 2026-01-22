package com.hospital.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton Helper class to manage Database connections.
 * Uses JDBC to connect to the MySQL database.
 */
public class DatabaseHelper {

    private static final String URL = "jdbc:mysql://localhost:3306/hospital_db";
    // TODO: Update these credentials according to your local MySQL setup
    private static final String USER = "root";
    private static final String PASSWORD = "hearty@01Heat";

    private static Connection connection = null;

    /**
     * Gets a connection to the database.
     * Implements a simple singleton pattern for the connection (or creates new if closed).
     *
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Ensure driver is loaded
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Database connected successfully.");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found!", e);
            }
        }
        return connection;
    }

    /**
     * Closes the current connection if it exists.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
