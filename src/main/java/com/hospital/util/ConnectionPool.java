package com.hospital.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Connection Pool implementation to manage database connections efficiently.
 * Implements a simple connection pool to avoid creating new connections on every request.
 * Follows the Singleton pattern and provides thread-safe connection management.
 */
public class ConnectionPool {
    
    private static final int INITIAL_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final long CONNECTION_TIMEOUT = 30; // seconds
    
    private static ConnectionPool instance;
    private final BlockingQueue<Connection> availableConnections;
    private final BlockingQueue<Connection> usedConnections;
    private final String url;
    private final String user;
    private final String password;
    private volatile boolean isShutdown = false;
    
    private ConnectionPool(String url, String user, String password) throws SQLException {
        this.url = url;
        this.user = user;
        this.password = password;
        this.availableConnections = new LinkedBlockingQueue<>();
        this.usedConnections = new LinkedBlockingQueue<>();
        
        // Load driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found!", e);
        }
        
        // Initialize pool
        initializePool();
    }
    
    /**
     * Gets the singleton instance of ConnectionPool.
     * 
     * @return ConnectionPool instance
     * @throws SQLException if initialization fails
     */
    public static synchronized ConnectionPool getInstance() throws SQLException {
        if (instance == null) {
            String url = "jdbc:mysql://localhost:3306/hospital_db";
            String user = "root";
            String password = "hearty@01Heat";
            instance = new ConnectionPool(url, user, password);
        }
        return instance;
    }
    
    /**
     * Initializes the connection pool with initial connections.
     */
    private void initializePool() throws SQLException {
        for (int i = 0; i < INITIAL_POOL_SIZE; i++) {
            Connection connection = createConnection();
            availableConnections.offer(connection);
        }
        System.out.println("Connection pool initialized with " + INITIAL_POOL_SIZE + " connections.");
    }
    
    /**
     * Creates a new database connection.
     */
    private Connection createConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        // Set auto-commit to true by default
        connection.setAutoCommit(true);
        return connection;
    }
    
    /**
     * Gets a connection from the pool.
     * 
     * @return Connection object
     * @throws SQLException if connection cannot be obtained
     */
    public Connection getConnection() throws SQLException {
        if (isShutdown) {
            throw new SQLException("Connection pool is shutdown");
        }
        
        Connection connection = null;
        
        try {
            // Try to get an available connection
            connection = availableConnections.poll(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
            
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                // Connection is invalid, create a new one if pool not at max
                if (getTotalConnections() < MAX_POOL_SIZE) {
                    connection = createConnection();
                } else {
                    // Wait a bit more for a connection to become available
                    connection = availableConnections.poll(CONNECTION_TIMEOUT, TimeUnit.SECONDS);
                    if (connection == null) {
                        throw new SQLException("Connection pool exhausted. No available connections.");
                    }
                }
            }
            
            usedConnections.offer(connection);
            return connection;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrupted while waiting for connection", e);
        }
    }
    
    /**
     * Returns a connection to the pool.
     * 
     * @param connection Connection to return
     */
    public void releaseConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        
        if (usedConnections.remove(connection)) {
            try {
                if (!connection.isClosed() && connection.isValid(2)) {
                    // Reset connection state
                    if (!connection.getAutoCommit()) {
                        connection.rollback();
                        connection.setAutoCommit(true);
                    }
                    availableConnections.offer(connection);
                } else {
                    // Connection is invalid, close it
                    connection.close();
                }
            } catch (SQLException e) {
                // Connection is invalid, try to close it
                try {
                    connection.close();
                } catch (SQLException ex) {
                    // Ignore
                }
            }
        }
    }
    
    /**
     * Gets the total number of connections (available + used).
     */
    private int getTotalConnections() {
        return availableConnections.size() + usedConnections.size();
    }
    
    /**
     * Shuts down the connection pool and closes all connections.
     */
    public synchronized void shutdown() {
        isShutdown = true;
        
        // Close all available connections
        closeConnections(availableConnections);
        closeConnections(usedConnections);
        
        availableConnections.clear();
        usedConnections.clear();
        
        System.out.println("Connection pool shutdown complete.");
    }
    
    /**
     * Closes all connections in a queue.
     */
    private void closeConnections(BlockingQueue<Connection> connections) {
        Connection connection;
        while ((connection = connections.poll()) != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                // Ignore
            }
        }
    }
}
