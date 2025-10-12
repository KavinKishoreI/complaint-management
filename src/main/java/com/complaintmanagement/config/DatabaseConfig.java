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
            throw new RuntimeException("Database configuration failed", e);
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
            String value = appProperties.getProperty(mapping[0]);
            if (value != null) {
                databaseProperties.setProperty(mapping[1], value);
            }
        }
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
            throw new IllegalStateException("EntityManagerFactory not initialized");
        }
        return entityManagerFactory;
    }
    
    /**
     * Test database connectivity
     */
    public static boolean testConnection() {
        try {
            EntityManagerFactory emf = getEntityManagerFactory();
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
