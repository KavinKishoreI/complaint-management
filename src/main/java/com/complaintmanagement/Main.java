package com.complaintmanagement;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.List;

import com.complaintmanagement.model.Complaint;
import com.complaintmanagement.model.Citizen;

/**
 * Main application class for the Complaint Management System.
 * This JavaFX application provides a user interface for managing customer complaints.
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Create the main layout
        VBox root = new VBox(30);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        // Title
        Label titleLabel = new Label("Complaint Management System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        // Subtitle
        Label subtitleLabel = new Label("Welcome to the Complaint Management Dashboard");
        subtitleLabel.setFont(Font.font("Arial", 16));
        subtitleLabel.setStyle("-fx-text-fill: #7f8c8d;");
        
        // Create dummy buttons for main functionality
        VBox buttonContainer = new VBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPrefWidth(300);
        
        Button viewComplaintsBtn = createStyledButton("View All Complaints");
        Button addComplaintBtn = createStyledButton("Add New Complaint");
        Button manageCitizensBtn = createStyledButton("Manage Citizens");
        Button manageAuthoritiesBtn = createStyledButton("Manage Authorities");
        Button reportsBtn = createStyledButton("Generate Reports");
        
        // Add event handlers
        viewComplaintsBtn.setOnAction(e -> viewAllComplaints());
        addComplaintBtn.setOnAction(e -> showMessage("Add Complaint feature - Coming Soon!"));
        manageCitizensBtn.setOnAction(e -> showMessage("Manage Citizens feature - Coming Soon!"));
        manageAuthoritiesBtn.setOnAction(e -> showMessage("Manage Authorities feature - Coming Soon!"));
        reportsBtn.setOnAction(e -> showMessage("Reports feature - Coming Soon!"));
        
        buttonContainer.getChildren().addAll(
            viewComplaintsBtn, 
            addComplaintBtn, 
            manageCitizensBtn, 
            manageAuthoritiesBtn, 
            reportsBtn
        );
        
        // Status label
        Label statusLabel = new Label("Database Connected Successfully");
        statusLabel.setFont(Font.font("Arial", 12));
        statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        
        root.getChildren().addAll(titleLabel, subtitleLabel, buttonContainer, statusLabel);
        
        Scene scene = new Scene(root, 900, 700);
        
        primaryStage.setTitle("Complaint Management System - Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        System.out.println("Complaint Management System started with dummy UI");
    }
    
    /**
     * Create a styled button for the main dashboard
     */
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(280);
        button.setPrefHeight(45);
        button.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        button.setStyle(
            "-fx-background-color: #3498db; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"
        );
        
        // Add hover effect
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: #2980b9; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: #3498db; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);"
        ));
        
        return button;
    }
    
    /**
     * View all complaints from the database and print to terminal
     */
    private void viewAllComplaints() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                           VIEW ALL COMPLAINTS");
        System.out.println("=".repeat(80));
        
        EntityManagerFactory emf = null;
        EntityManager em = null;
        
        try {
            // Create EntityManagerFactory and EntityManager
            emf = Persistence.createEntityManagerFactory("complaint-management-pu");
            em = emf.createEntityManager();
            
            // Query all complaints with related citizen information
            String jpql = "SELECT c FROM Complaint c " +
                         "LEFT JOIN FETCH c.citizen " +
                         "ORDER BY c.complaint_date DESC";
            
            TypedQuery<Complaint> query = em.createQuery(jpql, Complaint.class);
            List<Complaint> complaints = query.getResultList();
            
            if (complaints.isEmpty()) {
                System.out.println("No complaints found in the database.");
                System.out.println("\nTo add some test data, you can insert complaints directly into the database");
                System.out.println("or use the 'Add New Complaint' feature (coming soon).");
            } else {
                System.out.println("Found " + complaints.size() + " complaint(s):\n");
                
                for (int i = 0; i < complaints.size(); i++) {
                    Complaint complaint = complaints.get(i);
                    
                    System.out.println("Complaint #" + (i + 1));
                    System.out.println("-".repeat(50));
                    System.out.println("ID: " + complaint.getComplaint_id());
                    System.out.println("Title: " + complaint.getComplaint_title());
                    System.out.println("Description: " + complaint.getComplaint_descp());
                    System.out.println("Type/Category: " + complaint.getComplaint_type());
                    System.out.println("Department Concerned: " + complaint.getDept_concerned());
                    System.out.println("Status: " + complaint.getResolve_status());
                    System.out.println("Date Submitted: " + 
                        (complaint.getComplaint_date() != null ? 
                         complaint.getComplaint_date().toString() : "N/A"));
                    
                    // Display citizen information
                    Citizen citizen = complaint.getCitizen();
                    if (citizen != null) {
                        System.out.println("Submitted by: " + citizen.getCitizen_name());
                        System.out.println("Citizen Email: " + citizen.getCitizen_email());
                        System.out.println("Citizen Phone: " + citizen.getCitizen_phone());
                        System.out.println("Citizen City: " + citizen.getCitizen_city());
                        System.out.println("Citizen Pincode: " + citizen.getCitizen_pincode());
                    } else {
                        System.out.println("Submitted by: Unknown citizen");
                    }
                    
                    if (i < complaints.size() - 1) {
                        System.out.println("\n" + "~".repeat(50) + "\n");
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error fetching complaints from database:");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up resources
            if (em != null) {
                em.close();
            }
            if (emf != null) {
                emf.close();
            }
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                         END OF COMPLAINT LIST");
        System.out.println("=".repeat(80) + "\n");
    }

    /**
     * Show a simple message dialog (placeholder for now)
     */
    private void showMessage(String message) {
        System.out.println("UI Action: " + message);
        // TODO: Implement proper dialog or navigation
    }

    public static void main(String[] args) {
        System.out.println("Starting Complaint Management System...");
        launch(args);
    }
}
