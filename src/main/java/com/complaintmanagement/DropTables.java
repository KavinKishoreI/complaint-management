package com.complaintmanagement;

import java.sql.*;

public class DropTables {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String username = "system";
        String password = "kavinkishore";
        
        String[] dropStatements = {
            "DROP TABLE Response CASCADE CONSTRAINTS",
            "DROP TABLE Complaint CASCADE CONSTRAINTS",
            "DROP TABLE Authority CASCADE CONSTRAINTS",
            "DROP TABLE Citizen CASCADE CONSTRAINTS",
            "DROP TABLE Department CASCADE CONSTRAINTS"
        };
        
        try (Connection conn = DriverManager.getConnection(url, username, password);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("Connected to Oracle database");
            System.out.println("Dropping old tables...\n");
            
            for (String sql : dropStatements) {
                try {
                    stmt.execute(sql);
                    System.out.println("✓ Executed: " + sql);
                } catch (SQLException e) {
                    // Table might not exist, that's okay
                    System.out.println("⚠ " + sql + " - " + e.getMessage().split("\n")[0]);
                }
            }
            
            System.out.println("\n✓ All tables dropped successfully!");
            System.out.println("You can now run DatabaseTest to create fresh tables with the new 3NF schema.");
            
        } catch (SQLException e) {
            System.err.println("Database connection failed:");
            e.printStackTrace();
        }
    }
}
