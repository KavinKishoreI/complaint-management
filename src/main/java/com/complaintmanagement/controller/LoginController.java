package com.complaintmanagement.controller;

import com.complaintmanagement.model.Authority;
import com.complaintmanagement.model.Citizen;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;


/**
 * Controller class for the Login page.
 * Handles user authentication for both Citizens and Authorities.
 */
public class LoginController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Button clearButton;
    
    @FXML
    private Label statusLabel;
    
    private EntityManagerFactory emf;
    private EntityManager em;
    
    /**
     * Initialize method called after FXML loading
     */
    @FXML
    public void initialize() {
        // Initialize JPA EntityManager
        try {
            emf = Persistence.createEntityManagerFactory("complaint-management-pu");
            em = emf.createEntityManager();
            System.out.println("LoginController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing EntityManager: " + e.getMessage());
            e.printStackTrace();
            showError("Database connection error. Please contact administrator.");
        }
        
        // Add Enter key listener for login
        passwordField.setOnAction(event -> handleLogin(event));
    }
    
    /**
     * Handles the login button click
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }
        
        // Disable login button to prevent multiple clicks
        loginButton.setDisable(true);
        statusLabel.setVisible(false);
        
        try {
            // Try to authenticate as Citizen first
            Citizen citizen = authenticateCitizen(username, password);
            if (citizen != null) {
                showSuccess("Login successful! Welcome, " + citizen.getCitizen_name());
                // TODO: Navigate to Citizen Dashboard
                openDashboard("Citizen", citizen.getCitizen_id(), citizen.getCitizen_name());
                return;
            }
            
            // Try to authenticate as Authority
            Authority authority = authenticateAuthority(username, password);
            if (authority != null) {
                showSuccess("Login successful! Welcome, " + authority.getAuthority_designation());
                // TODO: Navigate to Authority Dashboard
                openDashboard("Authority", authority.getAuthority_id(), authority.getAuthority_username());
                return;
            }
            
            // If neither authentication succeeds
            showError("Invalid username or password");
            
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            showError("An error occurred during login. Please try again.");
        } finally {
            loginButton.setDisable(false);
        }
    }
    
    /**
     * Authenticates a citizen user
     */
    private Citizen authenticateCitizen(String username, String password) {
        try {
            TypedQuery<Citizen> query = em.createQuery(
                "SELECT c FROM Citizen c WHERE c.citizen_username = :username", 
                Citizen.class
            );
            query.setParameter("username", username);
            
            Citizen citizen = query.getResultList().stream().findFirst().orElse(null);
            
            if (citizen != null && citizen.getCitizen_password().equals(password)) {
                System.out.println("Citizen authenticated: " + username);
                return citizen;
            }
        } catch (Exception e) {
            System.err.println("Error authenticating citizen: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Authenticates an authority user
     */
    private Authority authenticateAuthority(String username, String password) {
        try {
            TypedQuery<Authority> query = em.createQuery(
                "SELECT a FROM Authority a WHERE a.authority_username = :username", 
                Authority.class
            );
            query.setParameter("username", username);
            
            Authority authority = query.getResultList().stream().findFirst().orElse(null);
            
            if (authority != null && authority.getAuthority_password().equals(password)) {
                System.out.println("Authority authenticated: " + username);
                return authority;
            }
        } catch (Exception e) {
            System.err.println("Error authenticating authority: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Opens the dashboard after successful login
     */
    private void openDashboard(String userType, Long userId, String userName) {
        try {
            if ("Authority".equals(userType)) {
                // Open Authority Dashboard
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AuthorityDashboard.fxml"));
                Parent root = loader.load();
                
                // Get the authority dashboard controller and pass information
                AuthorityDashboardController authorityController = loader.getController();
                authorityController.initializeAuthority(userId, userName);
                
                // Get current stage and set new scene
                Stage stage = (Stage) loginButton.getScene().getWindow();
                Scene scene = new Scene(root, 1000, 700);
                scene.getStylesheets().add(getClass().getResource("/css/authority-dashboard.css").toExternalForm());
                stage.setScene(scene);
                stage.setTitle("Complaint Management System - Authority Dashboard");
                stage.setResizable(true);
                stage.show();
                
                System.out.println("Authority Dashboard opened for: " + userName);
                
            } else {
                // Open Citizen Dashboard
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
                Parent root = loader.load();
                
                // Get the dashboard controller and pass user information
                DashboardController dashboardController = loader.getController();
                dashboardController.initializeUser(userType, userId, userName);
                
                // Get current stage and set new scene
                Stage stage = (Stage) loginButton.getScene().getWindow();
                Scene scene = new Scene(root, 1000, 700);
                stage.setScene(scene);
                stage.setTitle("Complaint Management System - Dashboard");
                stage.setResizable(true);
                stage.show();
                
                System.out.println("Dashboard opened for " + userType + ": " + userName);
            }
            
        } catch (Exception e) {
            System.err.println("Error opening dashboard: " + e.getMessage());
            e.printStackTrace();
            showError("Could not open dashboard. Please try again.");
        }
    }
    
    /**
     * Handles the clear button click
     */
    @FXML
    private void handleClear(ActionEvent event) {
        usernameField.clear();
        passwordField.clear();
        statusLabel.setVisible(false);
        usernameField.requestFocus();
        System.out.println("Login form cleared");
    }
    
    /**
     * Shows an error message
     */
    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        statusLabel.setVisible(true);
    }
    
    /**
     * Shows a success message
     */
    private void showSuccess(String message) {
        statusLabel.setText("✓ " + message);
        statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        statusLabel.setVisible(true);
    }
    
    /**
     * Cleanup method
     */
    public void cleanup() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
