package com.complaintmanagement;

import com.complaintmanagement.config.DatabaseConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

public class DatabaseConnection {

    public static EntityManager getEntityManager() {
        EntityManagerFactory emf = DatabaseConfig.getEntityManagerFactory();
        if (emf == null) {
            throw new IllegalStateException("Database not available");
        }
        return emf.createEntityManager();
    }

    public static void close() {
        DatabaseConfig.close();
    }

    public static boolean testConnection() {
        return DatabaseConfig.testConnection();
    }
}
