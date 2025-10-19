package com.complaintmanagement.controller;

import com.complaintmanagement.model.Complaint;
import com.complaintmanagement.model.Response;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Controller for viewing individual complaint details and responses
 */
public class ViewComplaintController {

    @FXML
    private Button btnBack;

    @FXML
    private Label lblUserInfo;

    @FXML
    private Label lblComplaintId;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblType;

    @FXML
    private Label lblDepartment;

    @FXML
    private Label lblTitle;

    @FXML
    private TextArea txtDescription;

    @FXML
    private Label lblStatus;

    @FXML
    private Label lblResponseCount;

    @FXML
    private VBox responsesContainer;

    @FXML
    private Label lblNoResponses;

    @FXML
    private Button btnRefresh;

    @FXML
    private Button btnClose;

    private EntityManagerFactory emf;
    private EntityManager em;

    private Long complaintId;
    private Long citizenId;
    private String citizenName;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        try {
            emf = Persistence.createEntityManagerFactory("complaint-management-pu");
            em = emf.createEntityManager();
            System.out.println("ViewComplaintController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing EntityManager: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialize with user and complaint information
     */
    public void initializeData(Long complaintId, Long citizenId, String citizenName) {
        this.complaintId = complaintId;
        this.citizenId = citizenId;
        this.citizenName = citizenName;
        
        lblUserInfo.setText("Citizen: " + citizenName);
        
        loadComplaintDetails();
    }

    /**
     * Load complaint details and responses
     */
    private void loadComplaintDetails() {
        try {
            // Load complaint
            Complaint complaint = em.find(Complaint.class, complaintId);
            
            if (complaint == null) {
                showError("Complaint not found");
                return;
            }

            // Populate complaint details
            lblComplaintId.setText("#" + complaint.getComplaint_id());
            lblTitle.setText(complaint.getComplaint_title());
            lblType.setText(complaint.getComplaint_type());
            lblDepartment.setText(complaint.getDepartment().getDept_name());
            txtDescription.setText(complaint.getComplaint_descp());
            
            // Format and display date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a");
            lblDate.setText(dateFormat.format(complaint.getComplaint_date()));
            
            // Set status with appropriate styling
            String status = complaint.getResolve_status().getDisplayName();
            lblStatus.setText(status);
            lblStatus.getStyleClass().clear();
            lblStatus.getStyleClass().add("status-badge");
            
            // Apply status-specific styling
            switch (complaint.getResolve_status()) {
                case UNREAD:
                    lblStatus.setStyle("-fx-background-color: #e74c3c;"); // Red
                    break;
                case READ:
                    lblStatus.setStyle("-fx-background-color: #f39c12;"); // Orange
                    break;
                case IN_PROGRESS:
                    lblStatus.setStyle("-fx-background-color: #3498db;"); // Blue
                    break;
                case CLOSED:
                    lblStatus.setStyle("-fx-background-color: #27ae60;"); // Green
                    break;
            }
            
            // Load responses
            loadResponses();
            
            System.out.println("Complaint details loaded: " + complaint.getComplaint_title());
            
        } catch (Exception e) {
            System.err.println("Error loading complaint details: " + e.getMessage());
            e.printStackTrace();
            showError("Failed to load complaint details");
        }
    }

    /**
     * Load responses for this complaint
     */
    private void loadResponses() {
        try {
            // Query responses for this complaint
            TypedQuery<Response> query = em.createQuery(
                "SELECT r FROM Response r WHERE r.complaint.complaint_id = :complaintId ORDER BY r.response_date ASC",
                Response.class
            );
            query.setParameter("complaintId", complaintId);
            List<Response> responses = query.getResultList();
            
            // Clear existing responses
            responsesContainer.getChildren().clear();
            
            if (responses.isEmpty()) {
                lblNoResponses.setVisible(true);
                lblResponseCount.setText("(0)");
            } else {
                lblNoResponses.setVisible(false);
                lblResponseCount.setText("(" + responses.size() + ")");
                
                // Add each response to the container
                for (Response response : responses) {
                    VBox responseCard = createResponseCard(response);
                    responsesContainer.getChildren().add(responseCard);
                }
            }
            
            System.out.println("Loaded " + responses.size() + " responses");
            
        } catch (Exception e) {
            System.err.println("Error loading responses: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create a visual card for a response
     */
    private VBox createResponseCard(Response response) {
        VBox card = new VBox(10);
        card.getStyleClass().add("response-card");
        card.setPadding(new Insets(15));
        
        // Header with authority name and date
        HBox header = new HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label authorityLabel = new Label("👤 " + response.getAuthority().getAuthority_username());
        authorityLabel.getStyleClass().add("authority-name");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a");
        Label dateLabel = new Label(dateFormat.format(response.getResponse_date()));
        dateLabel.getStyleClass().add("response-date");
        
        header.getChildren().addAll(authorityLabel, spacer, dateLabel);
        
        // Response text
        Label responseText = new Label(response.getResponse());
        responseText.getStyleClass().add("response-text");
        responseText.setWrapText(true);
        responseText.setMaxWidth(Double.MAX_VALUE);
        
        card.getChildren().addAll(header, responseText);
        
        return card;
    }

    /**
     * Handle back button
     */
    @FXML
    private void handleBack(ActionEvent event) {
        navigateBackToDashboard();
    }

    /**
     * Handle refresh button
     */
    @FXML
    private void handleRefresh(ActionEvent event) {
        System.out.println("Refreshing complaint details...");
        
        // Refresh the entity manager to get latest data
        em.clear();
        
        loadComplaintDetails();
    }

    /**
     * Navigate back to dashboard
     */
    private void navigateBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Parent root = loader.load();
            
            // Get controller and pass user information
            DashboardController dashboardController = loader.getController();
            dashboardController.initializeUser("Citizen", citizenId, citizenName);
            
            // Get current stage and set new scene
            Stage stage = (Stage) btnBack.getScene().getWindow();
            Scene scene = new Scene(root, 1000, 700);
            stage.setScene(scene);
            stage.setTitle("Complaint Management System - Dashboard");
            stage.show();
            
            System.out.println("Navigated back to Dashboard");
            
        } catch (Exception e) {
            System.err.println("Error navigating to dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        System.err.println("Error: " + message);
        // Could add a visual error message here
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
