package com.hospital.util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Helper class to manage Database connections using Connection Pool.
 * Provides a simple interface for obtaining connections from the pool.
 * This class now delegates to ConnectionPool for efficient connection management.
 */
public class DatabaseHelper {

    private static ConnectionPool connectionPool;

    static {
        try {
            connectionPool = ConnectionPool.getInstance();
        } catch (SQLException e) {
            System.err.println("Failed to initialize connection pool: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets a connection from the connection pool.
     * The connection should be returned to the pool after use.
     *
     * @return Connection object from the pool
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (connectionPool == null) {
            connectionPool = ConnectionPool.getInstance();
        }
        return connectionPool.getConnection();
    }

    /**
     * Returns a connection to the pool.
     * This should be called after using a connection.
     *
     * @param connection Connection to return to the pool
     */
    public static void releaseConnection(Connection connection) {
        if (connectionPool != null && connection != null) {
            connectionPool.releaseConnection(connection);
        }
    }

    /**
     * Closes the connection pool and all connections.
     * Should be called when the application shuts down.
     */
    public static void shutdown() {
        if (connectionPool != null) {
            connectionPool.shutdown();
        }
    }
}
