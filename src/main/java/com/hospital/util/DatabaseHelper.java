package com.hospital.util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Helper class to manage Database connections using Connection Pool.
 * Provides a simple facade interface for obtaining and releasing connections from the pool.
 * 
 * This class acts as a wrapper around ConnectionPool, simplifying connection management
 * for DAO classes. It handles connection pool initialization and provides static methods
 * for getting and releasing connections without directly instantiating ConnectionPool.
 * 
 * Why: Encapsulates connection pool complexity, making it easier for DAOs to manage
 * database connections without knowing pool implementation details.
 */
public class DatabaseHelper {

    // Static reference to the connection pool instance
    // Initialized once when class is loaded
    private static ConnectionPool connectionPool;

    /**
     * Static initializer block that initializes the connection pool on class load.
     * This ensures the pool is ready before any DAO tries to get a connection.
     * If initialization fails, an error is logged but the application continues
     * (pool will be initialized lazily on first getConnection() call).
     */
    static {
        try {
            // Initialize connection pool singleton instance
            // This loads database credentials from .env file via Config class
            connectionPool = ConnectionPool.getInstance();
        } catch (SQLException e) {
            // Log error but don't fail - pool will be initialized lazily if needed
            System.err.println("Failed to initialize connection pool: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets a connection from the connection pool.
     * The connection should be returned to the pool after use using releaseConnection().
     * 
     * If the pool wasn't initialized in the static block, it will be initialized here
     * (lazy initialization fallback).
     *
     * @return Connection object from the pool, ready for database operations
     * @throws SQLException if connection pool initialization fails or no connections available
     */
    public static Connection getConnection() throws SQLException {
        // Lazy initialization: if pool wasn't initialized in static block, initialize now
        if (connectionPool == null) {
            connectionPool = ConnectionPool.getInstance();
        }
        // Get a connection from the pool (may wait if pool is exhausted)
        return connectionPool.getConnection();
    }

    /**
     * Returns a connection to the pool after use.
     * This should be called in a finally block after using a connection to ensure
     * the connection is always returned to the pool, even if an exception occurs.
     * 
     * The connection is validated before being returned. Invalid connections are
     * closed and removed from the pool.
     *
     * @param connection Connection to return to the pool (can be null, method handles it gracefully)
     */
    public static void releaseConnection(Connection connection) {
        // Only release if pool exists and connection is not null
        if (connectionPool != null && connection != null) {
            // Return connection to pool for reuse
            connectionPool.releaseConnection(connection);
        }
    }

    /**
     * Closes the connection pool and all active connections.
     * Should be called when the application shuts down to properly release
     * all database resources and close connections gracefully.
     * 
     * This method is idempotent - safe to call multiple times.
     */
    public static void shutdown() {
        if (connectionPool != null) {
            // Shutdown pool and close all connections
            connectionPool.shutdown();
        }
    }
}
