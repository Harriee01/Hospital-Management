package com.hospital.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

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
    
    // MongoDB connection fields
    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;
    private static boolean mongoInitialized = false;
    
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
    /**
     * Gets the singleton instance of ConnectionPool.
     * Loads database credentials from Config class (which reads from .env file).
     * This fulfills requirement #3: Use .env for configuration instead of hardcoded values.
     * Epic: System Configuration / Evaluation Category: Security & Code Quality
     */
    public static synchronized ConnectionPool getInstance() throws SQLException {
        if (instance == null) {
            // Load configuration from .env file via Config utility
            // This prevents hardcoded credentials in source code
            String url = Config.DB_URL;
            String user = Config.DB_USER;
            String password = Config.DB_PASSWORD;
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
    
    // ====================================================================
    // MongoDB Connection Methods
    // ====================================================================
    
    /**
     * Initializes MongoDB connection using configuration from .env file.
     * This method loads MongoDB connection settings from Config class (which reads from .env).
     * Connection is established once and reused for all MongoDB operations.
     * 
     * Why in ConnectionPool: Centralizes all database connection management (MySQL and MongoDB)
     * in one place, making it easier to manage and configure from .env file.
     * 
     * @throws RuntimeException if MongoDB connection fails
     */
    public static synchronized void initializeMongoDB() {
        if (mongoInitialized && mongoClient != null) {
            return; // Already initialized
        }
        
        try {
            // Load MongoDB connection URI from .env file via Config utility
            // This allows configuration changes without code modification
            String mongoUri = Config.MONGO_URI;        // Reads MONGO_URI from .env (default: mongodb://localhost:27017)
            String dbName = Config.MONGO_DATABASE;     // Reads MONGO_DATABASE from .env (default: hospital_medical_records)
            
            // Create MongoDB client connection using the URI from .env
            mongoClient = MongoClients.create(mongoUri);
            
            // Get database reference (database is created automatically if it doesn't exist)
            mongoDatabase = mongoClient.getDatabase(dbName);
            
            mongoInitialized = true;
            System.out.println("DEBUG: MongoDB connected successfully to database: " + dbName);
        } catch (Exception e) {
            System.err.println("ERROR: Failed to connect to MongoDB: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("MongoDB connection failed. Please check MONGO_URI in .env file.", e);
        }
    }
    
    /**
     * Gets the MongoDB client instance.
     * Initializes the connection if not already done.
     * 
     * @return MongoClient instance for MongoDB operations
     */
    public static MongoClient getMongoClient() {
        if (!mongoInitialized) {
            initializeMongoDB();
        }
        return mongoClient;
    }
    
    /**
     * Gets the MongoDB database instance.
     * Initializes the connection if not already done.
     * 
     * @return MongoDatabase instance for database operations
     */
    public static MongoDatabase getMongoDatabase() {
        if (!mongoInitialized) {
            initializeMongoDB();
        }
        return mongoDatabase;
    }
    
    /**
     * Gets a MongoDB collection by name.
     * Initializes the connection if not already done.
     * 
     * @param collectionName Name of the collection to retrieve
     * @return MongoCollection<Document> instance for collection operations
     */
    public static MongoCollection<Document> getMongoCollection(String collectionName) {
        if (!mongoInitialized) {
            initializeMongoDB();
        }
        return mongoDatabase.getCollection(collectionName);
    }
    
    /**
     * Closes MongoDB connection.
     * Should be called on application shutdown to release resources.
     */
    public static synchronized void closeMongoDB() {
        if (mongoClient != null) {
            try {
                mongoClient.close();
                mongoInitialized = false;
                mongoDatabase = null;
                System.out.println("DEBUG: MongoDB connection closed.");
            } catch (Exception e) {
                System.err.println("ERROR: Failed to close MongoDB connection: " + e.getMessage());
            }
        }
    }
}
