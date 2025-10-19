package com.complaintmanagement.controller;

import com.complaintmanagement.model.*;
import jakarta.persistence.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Controller for the Authority Respond to Complaint page.
 * Allows authorities to view complaint details and submit responses.
 */
public class AuthorityRespondController {

    @FXML private Label complaintIdLabel;
    @FXML private Label titleLabel;
    @FXML private Label typeLabel;
    @FXML private Label citizenLabel;
    @FXML private Label statusLabel;
    @FXML private Label dateLabel;
    @FXML private Label descriptionLabel;
    
    @FXML private VBox responsesContainer;
    @FXML private VBox responsesList;
    
    @FXML private TextArea responseTextArea;
    @FXML private Label errorLabel;
    @FXML private Button submitButton;
    @FXML private Button closeTicketButton;
    @FXML private Button backButton;

    private EntityManagerFactory emf;
    private EntityManager em;
    
    private Complaint complaint;
    private Authority authority;
    private Long authorityId;
    private String authorityName;

    /**
     * Initialize method called after FXML loading
     */
    @FXML
    public void initialize() {
        try {
            emf = Persistence.createEntityManagerFactory("complaint-management-pu");
            em = emf.createEntityManager();
            System.out.println("AuthorityRespondController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing EntityManager: " + e.getMessage());
            e.printStackTrace();
            showError("Database connection error. Please contact administrator.");
        }
    }

    /**
     * Initialize the controller with authority and complaint data
     */
    public void initializeData(Long authorityId, String authorityName, Long complaintId) {
        this.authorityId = authorityId;
        this.authorityName = authorityName;
        
        loadAuthorityData();
        loadComplaintData(complaintId);
        loadPreviousResponses(complaintId);
    }

    /**
     * Load authority entity from database
     */
    private void loadAuthorityData() {
        try {
            authority = em.find(Authority.class, authorityId);
            if (authority == null) {
                showError("Authority not found.");
                System.err.println("Authority with ID " + authorityId + " not found");
            }
        } catch (Exception e) {
            System.err.println("Error loading authority: " + e.getMessage());
            e.printStackTrace();
            showError("Error loading authority data.");
        }
    }

    /**
     * Load complaint details and display them
     */
    private void loadComplaintData(Long complaintId) {
        try {
            complaint = em.find(Complaint.class, complaintId);
            
            if (complaint != null) {
                // Update UI with complaint details
                complaintIdLabel.setText("Complaint ID: #" + complaint.getComplaint_id());
                titleLabel.setText(complaint.getComplaint_title());
                typeLabel.setText(complaint.getComplaint_type());
                citizenLabel.setText(complaint.getCitizen().getCitizen_name());
                statusLabel.setText(complaint.getResolve_status().toString());
                
                dateLabel.setText(new java.text.SimpleDateFormat("MMM dd, yyyy hh:mm a").format(complaint.getComplaint_date()));
                
                descriptionLabel.setText(complaint.getComplaint_descp());
                
                // Apply status-specific styling
                applyStatusStyling(complaint.getResolve_status());
                
                // Update UI based on complaint status
                updateUIBasedOnStatus(complaint.getResolve_status());
                
                System.out.println("Loaded complaint: " + complaint.getComplaint_title());
            } else {
                showError("Complaint not found.");
                System.err.println("Complaint with ID " + complaintId + " not found");
            }
        } catch (Exception e) {
            System.err.println("Error loading complaint: " + e.getMessage());
            e.printStackTrace();
            showError("Error loading complaint details.");
        }
    }

    /**
     * Load and display previous responses to this complaint
     */
    private void loadPreviousResponses(Long complaintId) {
        try {
            TypedQuery<Response> query = em.createQuery(
                "SELECT r FROM Response r WHERE r.complaint.complaintId = :complaintId ORDER BY r.responseDate DESC",
                Response.class
            );
            query.setParameter("complaintId", complaintId);
            List<Response> responses = query.getResultList();
            
            if (!responses.isEmpty()) {
                responsesContainer.setVisible(true);
                responsesContainer.setManaged(true);
                responsesList.getChildren().clear();
                
                for (Response response : responses) {
                    VBox responseItem = createResponseItem(response);
                    responsesList.getChildren().add(responseItem);
                }
                
                System.out.println("Loaded " + responses.size() + " previous responses");
            }
        } catch (Exception e) {
            System.err.println("Error loading responses: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create a UI component for displaying a response
     */
    private VBox createResponseItem(Response response) {
        VBox item = new VBox(8);
        item.getStyleClass().add("response-item");
        
        // Response header with authority name and date
        Label header = new Label("Response by: " + response.getAuthority().getAuthority_username());
        header.getStyleClass().add("response-header");
        
        Label date = new Label(new java.text.SimpleDateFormat("MMM dd, yyyy hh:mm a").format(response.getResponse_date()));
        date.getStyleClass().add("response-date");
        
        Label text = new Label(response.getResponse());
        text.getStyleClass().add("response-text");
        text.setWrapText(true);
        
        item.getChildren().addAll(header, date, text);
        return item;
    }

    /**
     * Apply status-specific styling to the status label
     */
    private void applyStatusStyling(ComplaintStatus status) {
        statusLabel.getStyleClass().clear();
        statusLabel.getStyleClass().add("status-badge");
        
        switch (status) {
            case UNREAD:
                statusLabel.setStyle("-fx-background-color: #e74c3c;");
                break;
            case READ:
                statusLabel.setStyle("-fx-background-color: #3498db;");
                break;
            case IN_PROGRESS:
                statusLabel.setStyle("-fx-background-color: #f39c12;");
                break;
            case CLOSED:
                statusLabel.setStyle("-fx-background-color: #27ae60;");
                break;
        }
    }

    /**
     * Update UI elements based on complaint status
     */
    private void updateUIBasedOnStatus(ComplaintStatus status) {
        if (status == ComplaintStatus.CLOSED) {
            // Disable response submission and close button if already closed
            responseTextArea.setDisable(true);
            responseTextArea.setPromptText("This complaint has been closed. No further responses can be added.");
            submitButton.setDisable(true);
            closeTicketButton.setDisable(true);
            closeTicketButton.setText("Already Closed");
        } else {
            // Enable all controls for open complaints
            responseTextArea.setDisable(false);
            submitButton.setDisable(false);
            closeTicketButton.setDisable(false);
            closeTicketButton.setText("Close Ticket");
        }
    }

    /**
     * Handle submit response button click
     */
    @FXML
    private void handleSubmitResponse() {
        String responseText = responseTextArea.getText().trim();
        
        // Validation
        if (responseText.isEmpty()) {
            showError("Please enter a response message.");
            return;
        }
        
        if (complaint == null || authority == null) {
            showError("Error: Missing complaint or authority data.");
            return;
        }
        
        try {
            em.getTransaction().begin();
            
            // Get max response_id for this complaint and authority
            TypedQuery<Long> maxQuery = em.createQuery(
                "SELECT COALESCE(MAX(r.id.response_id), 0) FROM Response r WHERE r.id.complaint_id = :complaintId AND r.id.authority_id = :authorityId",
                Long.class
            );
            maxQuery.setParameter("complaintId", complaint.getComplaint_id());
            maxQuery.setParameter("authorityId", authority.getAuthority_id());
            Long maxResponseId = maxQuery.getSingleResult();
            
            // Create ResponseId (composite key)
            ResponseId responseId = new ResponseId(complaint.getComplaint_id(), authority.getAuthority_id(), maxResponseId + 1);
            
            // Create new response
            Response response = new Response();
            response.setId(responseId);
            response.setComplaint(complaint);
            response.setAuthority(authority);
            response.setResponse(responseText);
            response.setResponse_date(new java.util.Date());
            
            // Persist the response
            em.persist(response);
            
            // Update complaint status to IN_PROGRESS if it was UNREAD or READ
            if (complaint.getResolve_status() == ComplaintStatus.UNREAD || 
                complaint.getResolve_status() == ComplaintStatus.READ) {
                complaint.setResolve_status(ComplaintStatus.IN_PROGRESS);
                em.merge(complaint);
            }
            
            em.getTransaction().commit();
            
            System.out.println("Response submitted successfully for complaint #" + complaint.getComplaint_id());
            
            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Response Submitted");
            alert.setContentText("Your response has been submitted successfully!");
            alert.showAndWait();
            
            // Navigate back to dashboard
            handleBack();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error submitting response: " + e.getMessage());
            e.printStackTrace();
            showError("Error submitting response. Please try again.");
        }
    }

    /**
     * Handle close ticket button click
     */
    @FXML
    private void handleCloseTicket() {
        if (complaint == null || authority == null) {
            showError("Error: Missing complaint or authority data.");
            return;
        }
        
        // Confirm with the authority
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Close Ticket");
        confirmAlert.setHeaderText("Close Complaint #" + complaint.getComplaint_id());
        confirmAlert.setContentText("Are you sure you want to close this complaint?\n\nThis will mark the complaint as RESOLVED/CLOSED.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                closeComplaint();
            }
        });
    }

    /**
     * Close the complaint and update status to CLOSED
     */
    private void closeComplaint() {
        try {
            em.getTransaction().begin();
            
            // Update complaint status to CLOSED
            complaint.setResolve_status(ComplaintStatus.CLOSED);
            em.merge(complaint);
            
            em.getTransaction().commit();
            
            System.out.println("Complaint #" + complaint.getComplaint_id() + " closed successfully");
            
            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Complaint Closed");
            alert.setContentText("The complaint has been successfully closed!");
            alert.showAndWait();
            
            // Navigate back to dashboard
            handleBack();
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error closing complaint: " + e.getMessage());
            e.printStackTrace();
            showError("Error closing complaint. Please try again.");
        }
    }

    /**
     * Handle back button click
     */
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AuthorityDashboard.fxml"));
            Parent root = loader.load();
            
            // Pass authority data to dashboard controller
            AuthorityDashboardController controller = loader.getController();
            controller.initializeAuthority(authorityId, authorityName);
            
            // Get current stage and set new scene
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/authority-dashboard.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Authority Dashboard");
            
            System.out.println("Navigated back to Authority Dashboard");
            
        } catch (IOException e) {
            System.err.println("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
            showError("Error navigating to dashboard.");
        }
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    /**
     * Cleanup resources when controller is destroyed
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
