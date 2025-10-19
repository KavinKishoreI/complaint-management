package com.complaintmanagement.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Simple database configuration class for managing database connections.
 * This class handles loading configuration from multiple sources and provides
 * a centralized way to manage the EntityManagerFactory.
 */
public class DatabaseConfig {
    
    private static final String PERSISTENCE_UNIT_NAME = "complaint-management-pu";
    private static EntityManagerFactory entityManagerFactory;
    private static Properties databaseProperties;
    
    /**
     * Initialize database configuration on class loading
     */
    static {
        initializeDatabaseConfig();
    }
    
    /**
     * Initialize the database configuration
     */
    private static void initializeDatabaseConfig() {
        try {
            loadProperties();
            createEntityManagerFactory();
            logConfiguration();
        } catch (Exception e) {
            System.err.println("Failed to initialize database configuration: " + e.getMessage());
            System.err.println("The application will continue without database functionality.");
            // Don't throw exception - allow application to start without database
            entityManagerFactory = null;
        }
    }
    
    /**
     * Load database properties from application.properties and environment variables
     */
    private static void loadProperties() {
        databaseProperties = new Properties();
        
        // Load from application.properties
        try (InputStream input = DatabaseConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            
            if (input != null) {
                Properties appProperties = new Properties();
                appProperties.load(input);
                
                // Map application.properties to persistence properties
                mapApplicationProperties(appProperties);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load application.properties: " + e.getMessage());
        }
        
        // Override with environment variables
        overrideWithEnvironmentVariables();
    }
    
    /**
     * Map application.properties to persistence properties
     */
    private static void mapApplicationProperties(Properties appProperties) {
        String[][] mappings = {
            {"database.url", "jakarta.persistence.jdbc.url"},
            {"database.username", "jakarta.persistence.jdbc.user"},
            {"database.password", "jakarta.persistence.jdbc.password"},
            {"hibernate.dialect", "hibernate.dialect"},
            {"hibernate.hbm2ddl.auto", "hibernate.hbm2ddl.auto"},
            {"hibernate.show_sql", "hibernate.show_sql"},
            {"hibernate.format_sql", "hibernate.format_sql"},
            {"hibernate.connection.pool_size", "hibernate.connection.pool_size"},
            {"hibernate.connection.autocommit", "hibernate.connection.autocommit"}
        };
        
        for (String[] mapping : mappings) {
            String value = resolveProperty(appProperties.getProperty(mapping[0]));
            if (value != null) {
                databaseProperties.setProperty(mapping[1], value);
            }
        }
        
        // Always set Oracle JDBC driver for Oracle connections
        String dbUrl = databaseProperties.getProperty("jakarta.persistence.jdbc.url");
        if (dbUrl != null && dbUrl.contains("oracle")) {
            databaseProperties.setProperty("jakarta.persistence.jdbc.driver", "oracle.jdbc.OracleDriver");
        }
    }
    
    /**
     * Resolve property placeholders like ${ENV_VAR:default_value}
     */
    private static String resolveProperty(String value) {
        if (value == null) return null;
        
        // Handle ${ENV_VAR:default_value} syntax
        if (value.startsWith("${") && value.endsWith("}")) {
            String placeholder = value.substring(2, value.length() - 1);
            String[] parts = placeholder.split(":", 2);
            String envVar = parts[0];
            String defaultValue = parts.length > 1 ? parts[1] : null;
            
            String envValue = System.getenv(envVar);
            if (envValue != null && !envValue.trim().isEmpty()) {
                return envValue;
            } else {
                return defaultValue;
            }
        }
        
        return value;
    }
    
    /**
     * Override properties with environment variables
     */
    private static void overrideWithEnvironmentVariables() {
        String[][] envMappings = {
            {"DB_URL", "jakarta.persistence.jdbc.url"},
            {"DB_USERNAME", "jakarta.persistence.jdbc.user"},
            {"DB_PASSWORD", "jakarta.persistence.jdbc.password"},
            {"HIBERNATE_DDL_AUTO", "hibernate.hbm2ddl.auto"},
            {"HIBERNATE_SHOW_SQL", "hibernate.show_sql"},
            {"HIBERNATE_FORMAT_SQL", "hibernate.format_sql"},
            {"DB_POOL_SIZE", "hibernate.connection.pool_size"}
        };
        
        for (String[] mapping : envMappings) {
            String envValue = System.getenv(mapping[0]);
            if (envValue != null && !envValue.trim().isEmpty()) {
                databaseProperties.setProperty(mapping[1], envValue);
            }
        }
    }
    
    /**
     * Create the EntityManagerFactory
     */
    private static void createEntityManagerFactory() {
        entityManagerFactory = Persistence.createEntityManagerFactory(
            PERSISTENCE_UNIT_NAME, databaseProperties);
    }
    
    /**
     * Log the current configuration (without sensitive data)
     */
    private static void logConfiguration() {
        System.out.println("=== Database Configuration ===");
        System.out.println("Persistence Unit: " + PERSISTENCE_UNIT_NAME);
        System.out.println("URL: " + maskPassword(databaseProperties.getProperty("jakarta.persistence.jdbc.url")));
        System.out.println("Username: " + databaseProperties.getProperty("jakarta.persistence.jdbc.user"));
        System.out.println("Dialect: " + databaseProperties.getProperty("hibernate.dialect"));
        System.out.println("DDL Auto: " + databaseProperties.getProperty("hibernate.hbm2ddl.auto"));
        System.out.println("Show SQL: " + databaseProperties.getProperty("hibernate.show_sql"));
        System.out.println("=============================");
    }
    
    /**
     * Mask password in connection string
     */
    private static String maskPassword(String url) {
        if (url == null) return "null";
        return url.replaceAll("password=[^&]*", "password=***");
    }
    
    /**
     * Get the EntityManagerFactory
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            System.err.println("Database not available - EntityManagerFactory not initialized");
            return null;
        }
        return entityManagerFactory;
    }
    
    /**
     * Test database connectivity
     */
    public static boolean testConnection() {
        try {
            EntityManagerFactory emf = getEntityManagerFactory();
            if (emf == null) {
                System.err.println("❌ Database connection test failed: EntityManagerFactory not available");
                return false;
            }
            
            var em = emf.createEntityManager();
            em.getTransaction().begin();
            
            // Simple connection test query
            em.createNativeQuery("SELECT 1 FROM DUAL").getSingleResult();
            
            em.getTransaction().commit();
            em.close();
            
            System.out.println("✅ Database connection test successful");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Close the EntityManagerFactory
     */
    public static void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            System.out.println("Database configuration closed");
        }
    }
    
    /**
     * Get database properties for debugging
     */
    public static Properties getProperties() {
        return new Properties(databaseProperties);
    }
}
