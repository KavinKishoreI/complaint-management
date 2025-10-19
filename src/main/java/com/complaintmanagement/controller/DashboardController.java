package com.complaintmanagement.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller for the Dashboard FXML view.
 * This is a placeholder controller for the complaint management dashboard.
 */
public class DashboardController {

    @FXML
    private Label lblWelcome;
    
    @FXML
    private Button btnRegisterComplaint;
    
    @FXML
    private Button btnLogout;
    
    // User information passed from login
    private String userType;
    private Long userId;
    private String userName;

    /**
     * Initialize the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        System.out.println("Dashboard Controller initialized");
    }
    
    /**
     * Initialize user information
     */
    public void initializeUser(String userType, Long userId, String userName) {
        this.userType = userType;
        this.userId = userId;
        this.userName = userName;
        lblWelcome.setText("Welcome, " + userName + " (" + userType + ")");
        System.out.println("Dashboard initialized for user: " + userName);
    }
    
    /**
     * Handle Register Complaint button click
     */
    @FXML
    private void handleRegisterComplaint(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RegisterComplaint.fxml"));
            Parent root = loader.load();
            
            // Apply CSS
            Scene scene = new Scene(root, 1000, 700);
            scene.getStylesheets().add(getClass().getResource("/css/register-complaint.css").toExternalForm());
            
            // Pass user information to RegisterComplaintController
            RegisterComplaintController controller = loader.getController();
            if (userId != null && userName != null) {
                controller.initializeUser(userId, userName);
            }
            
            // Get current stage and set new scene
            Stage stage = (Stage) btnRegisterComplaint.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Complaint Management System - Register Complaint");
            stage.show();
            
            System.out.println("Navigated to Register Complaint page");
            
        } catch (Exception e) {
            System.err.println("Error opening Register Complaint page: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle Logout button click
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            
            // Apply CSS
            Scene scene = new Scene(root, 500, 650);
            scene.getStylesheets().add(getClass().getResource("/css/login.css").toExternalForm());
            
            // Get current stage and set new scene
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Complaint Management System - Login");
            stage.setResizable(false);
            stage.show();
            
            System.out.println("User logged out");
            
        } catch (Exception e) {
            System.err.println("Error logging out: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


