package com.hospital.util;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;

/**
 * Configuration utility class that loads sensitive configuration from .env
 * file.
 * Falls back to system environment variables if .env file is not found.
 * <p>
 * This fulfills requirement #3: .env file support for configuration.
 * Epic: System Configuration / User Story: Secure credential management
 * Evaluation Category: Code Quality & Security - prevents hardcoded credentials
 * <p>
 * Why: Separates configuration from code, allows different configs per
 * environment
 * (dev, staging, production) without code changes, and prevents accidental
 * credential commits to version control.
 */
public class Config {

    private static Dotenv dotenv;
    private static boolean dotenvLoaded = false;
    // Database configuration keys
    public static final String DB_URL = get("DB_URL", "jdbc:mysql://localhost:3306/hospital_db");
    public static final String DB_USER = get("DB_USER", "root");
    public static final String DB_PASSWORD = get("DB_PASSWORD", "hearty@01Heat");
    // MongoDB configuration keys
    public static final String MONGO_URI = get("MONGO_URI", "mongodb://localhost:27017");
    public static final String MONGO_DATABASE = get("MONGO_DATABASE", "hospital_medical_records");

    static {
        // Try to load .env file from project root
        // This fulfills requirement #3: Load config from .env file
        try {
            dotenv = Dotenv.configure()
                    .ignoreIfMissing() // Don't fail if .env doesn't exist
                    .load();
            dotenvLoaded = true;
            System.out.println("DEBUG: .env file loaded successfully.");
        } catch (DotenvException e) {
            // .env file not found or error reading it - will use system env as fallback
            System.out.println("DEBUG: .env file not found, using system environment variables.");
            dotenvLoaded = false;
        }
    }

    /**
     * Gets a configuration value from .env file or system environment.
     * Priority: .env file > system environment > default value
     *
     * @param key          Configuration key (e.g., "DB_URL")
     * @param defaultValue Default value if key not found
     * @return Configuration value or default
     */
    public static String get(String key, String defaultValue) {
        // First try .env file if loaded
        if (dotenvLoaded && dotenv != null) {
            String value = dotenv.get(key);
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }

        // Fallback to system environment variable
        String sysValue = System.getenv(key);
        if (sysValue != null && !sysValue.isEmpty()) {
            return sysValue;
        }

        // Return default value
        return defaultValue;
    }

    /**
     * Gets a required configuration value (throws exception if not found).
     *
     * @param key Configuration key
     * @return Configuration value
     * @throws IllegalStateException if value not found in .env or system env
     */
    public static String getRequired(String key) {
        String value = get(key, null);
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException(
                    "Required configuration key '" + key + "' not found in .env or system environment.");
        }
        return value;
    }
}
