package com.complaintmanagement.controller;

import com.complaintmanagement.model.Citizen;
import com.complaintmanagement.model.Complaint;
import com.complaintmanagement.model.ComplaintStatus;
import com.complaintmanagement.model.Department;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.Date;
import java.util.List;

/**
 * Controller class for the Register Complaint page.
 * Handles complaint registration form with validation (service layer to be added later).
 */
public class RegisterComplaintController {
    
    @FXML
    private Button backButton;
    
    @FXML
    private Label userInfoLabel;
    
    @FXML
    private TextField titleField;
    
    @FXML
    private ComboBox<String> complaintTypeCombo;
    
    @FXML
    private ComboBox<Department> departmentCombo;
    
    @FXML
    private TextArea descriptionArea;
    
    @FXML
    private Label charCountLabel;
    
    @FXML
    private Label titleErrorLabel;
    
    @FXML
    private Label typeErrorLabel;
    
    @FXML
    private Label deptErrorLabel;
    
    @FXML
    private Label descErrorLabel;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private Button submitButton;
    
    @FXML
    private Button clearButton;
    
    @FXML
    private Button cancelButton;
    
    private EntityManagerFactory emf;
    private EntityManager em;
    
    // Current logged-in citizen (will be passed from login/dashboard)
    private Citizen currentCitizen;
    private Long currentCitizenId;
    private String currentCitizenName;
    
    private static final int MAX_TITLE_LENGTH = 200;
    private static final int MAX_DESC_LENGTH = 1000;
    
    /**
     * Initialize method called after FXML loading
     */
    @FXML
    public void initialize() {
        // Initialize JPA EntityManager
        try {
            emf = Persistence.createEntityManagerFactory("complaint-management-pu");
            em = emf.createEntityManager();
            System.out.println("RegisterComplaintController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing EntityManager: " + e.getMessage());
            e.printStackTrace();
            showError("Database connection error. Please contact administrator.");
        }
        
        // Set up complaint types
        setupComplaintTypes();
        
        // Load departments from database
        loadDepartments();
        
        // Set up character counter for description
        setupCharacterCounter();
        
        // Set up field validation listeners
        setupValidationListeners();
        
        // Configure custom cell factory for department combo box
        departmentCombo.setCellFactory(lv -> new ListCell<Department>() {
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getDept_name());
            }
        });
        
        departmentCombo.setButtonCell(new ListCell<Department>() {
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getDept_name());
            }
        });
    }
    
    /**
     * Set up complaint types
     */
    private void setupComplaintTypes() {
        ObservableList<String> complaintTypes = FXCollections.observableArrayList(
            "Infrastructure Issue",
            "Public Safety",
            "Sanitation & Hygiene",
            "Water Supply",
            "Electricity",
            "Road Maintenance",
            "Drainage Problem",
            "Noise Pollution",
            "Air Pollution",
            "Illegal Construction",
            "Street Lighting",
            "Garbage Collection",
            "Public Transport",
            "Traffic Management",
            "Other"
        );
        complaintTypeCombo.setItems(complaintTypes);
    }
    
    /**
     * Load departments from database
     */
    private void loadDepartments() {
        try {
            TypedQuery<Department> query = em.createQuery(
                "SELECT d FROM Department d ORDER BY d.dept_name", 
                Department.class
            );
            List<Department> departments = query.getResultList();
            
            ObservableList<Department> deptList = FXCollections.observableArrayList(departments);
            departmentCombo.setItems(deptList);
            
            System.out.println("Loaded " + departments.size() + " departments");
        } catch (Exception e) {
            System.err.println("Error loading departments: " + e.getMessage());
            e.printStackTrace();
            showError("Could not load departments. Please refresh the page.");
        }
    }
    
    /**
     * Set up character counter for description field
     */
    private void setupCharacterCounter() {
        descriptionArea.textProperty().addListener((observable, oldValue, newValue) -> {
            int length = newValue.length();
            charCountLabel.setText(length + " / " + MAX_DESC_LENGTH + " characters");
            
            if (length > MAX_DESC_LENGTH) {
                charCountLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            } else if (length > MAX_DESC_LENGTH * 0.9) {
                charCountLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
            } else {
                charCountLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
            }
        });
    }
    
    /**
     * Set up validation listeners for fields
     */
    private void setupValidationListeners() {
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                titleErrorLabel.setVisible(false);
            }
        });
        
        complaintTypeCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                typeErrorLabel.setVisible(false);
            }
        });
        
        departmentCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                deptErrorLabel.setVisible(false);
            }
        });
        
        descriptionArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                descErrorLabel.setVisible(false);
            }
        });
    }
    
    /**
     * Initialize user information (to be called from previous page)
     */
    public void initializeUser(Long citizenId, String citizenName) {
        this.currentCitizenId = citizenId;
        this.currentCitizenName = citizenName;
        userInfoLabel.setText("Citizen: " + citizenName);
        
        // Load the full citizen entity
        try {
            currentCitizen = em.find(Citizen.class, citizenId);
            System.out.println("Citizen loaded: " + citizenName);
        } catch (Exception e) {
            System.err.println("Error loading citizen: " + e.getMessage());
        }
    }
    
    /**
     * Handles the submit button click
     */
    @FXML
    private void handleSubmit(ActionEvent event) {
        // Validate all fields
        if (!validateForm()) {
            return;
        }
        
        // Disable submit button to prevent multiple submissions
        submitButton.setDisable(true);
        statusLabel.setVisible(false);
        
        try {
            // Create new complaint object
            Complaint complaint = new Complaint();
            complaint.setComplaint_title(titleField.getText().trim());
            complaint.setComplaint_type(complaintTypeCombo.getValue());
            complaint.setDepartment(departmentCombo.getValue());
            complaint.setComplaint_descp(descriptionArea.getText().trim());
            complaint.setResolve_status(ComplaintStatus.UNREAD);
            complaint.setComplaint_date(new Date());
            
            // Set citizen (ensure we have the citizen loaded)
            if (currentCitizen == null && currentCitizenId != null) {
                currentCitizen = em.find(Citizen.class, currentCitizenId);
            }
            
            if (currentCitizen == null) {
                showError("User session expired. Please login again.");
                submitButton.setDisable(false);
                return;
            }
            
            complaint.setCitizen(currentCitizen);
            
            // Save complaint to database
            em.getTransaction().begin();
            em.persist(complaint);
            em.getTransaction().commit();
            
            System.out.println("Complaint submitted successfully with ID: " + complaint.getComplaint_id());
            
            // Show success message
            showSuccess("✓ Complaint submitted successfully! Reference ID: #" + complaint.getComplaint_id());
            
            // Clear form after 2 seconds and show confirmation
            javafx.application.Platform.runLater(() -> {
                try {
                    Thread.sleep(2000);
                    
                    // Show confirmation dialog
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Complaint Submitted");
                    alert.setHeaderText("Your complaint has been registered successfully!");
                    alert.setContentText(
                        "Reference ID: #" + complaint.getComplaint_id() + "\n" +
                        "Title: " + complaint.getComplaint_title() + "\n" +
                        "Department: " + complaint.getDepartment().getDept_name() + "\n" +
                        "Status: " + complaint.getResolve_status().getDisplayName() + "\n\n" +
                        "You can track your complaint using the reference ID."
                    );
                    alert.showAndWait();
                    
                    // Clear form or navigate back
                    handleClear(null);
                    
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error submitting complaint: " + e.getMessage());
            e.printStackTrace();
            showError("Failed to submit complaint. Please try again.");
        } finally {
            submitButton.setDisable(false);
        }
    }
    
    /**
     * Validate the form
     */
    private boolean validateForm() {
        boolean isValid = true;
        
        // Reset error messages
        titleErrorLabel.setVisible(false);
        typeErrorLabel.setVisible(false);
        deptErrorLabel.setVisible(false);
        descErrorLabel.setVisible(false);
        statusLabel.setVisible(false);
        
        // Validate title
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            titleErrorLabel.setText("❌ Title is required");
            titleErrorLabel.setVisible(true);
            isValid = false;
        } else if (title.length() > MAX_TITLE_LENGTH) {
            titleErrorLabel.setText("❌ Title must be less than " + MAX_TITLE_LENGTH + " characters");
            titleErrorLabel.setVisible(true);
            isValid = false;
        }
        
        // Validate complaint type
        if (complaintTypeCombo.getValue() == null) {
            typeErrorLabel.setText("❌ Please select a complaint type");
            typeErrorLabel.setVisible(true);
            isValid = false;
        }
        
        // Validate department
        if (departmentCombo.getValue() == null) {
            deptErrorLabel.setText("❌ Please select a department");
            deptErrorLabel.setVisible(true);
            isValid = false;
        }
        
        // Validate description
        String description = descriptionArea.getText().trim();
        if (description.isEmpty()) {
            descErrorLabel.setText("❌ Description is required");
            descErrorLabel.setVisible(true);
            isValid = false;
        } else if (description.length() < 20) {
            descErrorLabel.setText("❌ Description must be at least 20 characters");
            descErrorLabel.setVisible(true);
            isValid = false;
        } else if (description.length() > MAX_DESC_LENGTH) {
            descErrorLabel.setText("❌ Description must be less than " + MAX_DESC_LENGTH + " characters");
            descErrorLabel.setVisible(true);
            isValid = false;
        }
        
        if (!isValid) {
            showWarning("Please fill in all required fields correctly.");
        }
        
        return isValid;
    }
    
    /**
     * Handles the clear button click
     */
    @FXML
    private void handleClear(ActionEvent event) {
        titleField.clear();
        complaintTypeCombo.setValue(null);
        departmentCombo.setValue(null);
        descriptionArea.clear();
        
        titleErrorLabel.setVisible(false);
        typeErrorLabel.setVisible(false);
        deptErrorLabel.setVisible(false);
        descErrorLabel.setVisible(false);
        statusLabel.setVisible(false);
        
        titleField.requestFocus();
        System.out.println("Complaint form cleared");
    }
    
    /**
     * Handles the cancel button click
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        // Confirm before canceling if form has data
        if (hasFormData()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Cancel");
            alert.setHeaderText("Discard Changes?");
            alert.setContentText("You have unsaved data. Are you sure you want to cancel?");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    navigateBack();
                }
            });
        } else {
            navigateBack();
        }
    }
    
    /**
     * Handles the back button click
     */
    @FXML
    private void handleBack(ActionEvent event) {
        handleCancel(event);
    }
    
    /**
     * Check if form has any data
     */
    private boolean hasFormData() {
        return !titleField.getText().trim().isEmpty() ||
               complaintTypeCombo.getValue() != null ||
               departmentCombo.getValue() != null ||
               !descriptionArea.getText().trim().isEmpty();
    }
    
    /**
     * Navigate back to dashboard
     */
    private void navigateBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Parent root = loader.load();
            
            // Get current stage and set new scene
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 700);
            stage.setScene(scene);
            stage.setTitle("Complaint Management System - Dashboard");
            stage.show();
            
            System.out.println("Navigated back to Dashboard");
            
        } catch (Exception e) {
            System.err.println("Error navigating to dashboard: " + e.getMessage());
            e.printStackTrace();
            showError("Could not navigate to dashboard. Please try again.");
        }
    }
    
    /**
     * Shows an error message
     */
    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.getStyleClass().clear();
        statusLabel.getStyleClass().addAll("status-label", "status-error");
        statusLabel.setVisible(true);
    }
    
    /**
     * Shows a success message
     */
    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().clear();
        statusLabel.getStyleClass().addAll("status-label", "status-success");
        statusLabel.setVisible(true);
    }
    
    /**
     * Shows a warning message
     */
    private void showWarning(String message) {
        statusLabel.setText("⚠ " + message);
        statusLabel.getStyleClass().clear();
        statusLabel.getStyleClass().addAll("status-label", "status-warning");
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
