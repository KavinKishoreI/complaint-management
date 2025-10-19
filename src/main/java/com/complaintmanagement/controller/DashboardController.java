package com.complaintmanagement.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.complaintmanagement.model.Complaint;
import com.complaintmanagement.model.ComplaintStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for the Dashboard FXML view.
 * Displays statistics, recent complaints, and provides navigation.
 */
public class DashboardController {

    @FXML
    private Label lblWelcome;
    
    @FXML
    private Label lblTotalComplaints;
    
    @FXML
    private Label lblPendingComplaints;
    
    @FXML
    private Label lblInProgressComplaints;
    
    @FXML
    private Label lblResolvedComplaints;
    
    @FXML
    private Label lblLastUpdated;
    
    @FXML
    private Label lblTableInfo;
    
    @FXML
    private Button btnRegisterComplaint;
    
    @FXML
    private Button btnRefresh;
    
    @FXML
    private Button btnLogout;
    
    @FXML
    private TableView<ComplaintRow> tblRecentComplaints;
    
    @FXML
    private TableColumn<ComplaintRow, String> colId;
    
    @FXML
    private TableColumn<ComplaintRow, String> colTitle;
    
    @FXML
    private TableColumn<ComplaintRow, String> colType;
    
    @FXML
    private TableColumn<ComplaintRow, String> colDepartment;
    
    @FXML
    private TableColumn<ComplaintRow, String> colStatus;
    
    @FXML
    private TableColumn<ComplaintRow, String> colDate;
    
    // User information passed from login
    private String userType;
    private Long userId;
    private String userName;
    
    private EntityManagerFactory emf;

    /**
     * Initialize the controller.
     * This method is automatically called after the FXML file has been loaded.
     */
    @FXML
    public void initialize() {
        System.out.println("Dashboard Controller initialized");
        
        // Initialize EntityManagerFactory
        emf = Persistence.createEntityManagerFactory("complaint-management-pu");
        
        // Setup table columns
        setupTableColumns();
    }
    
    /**
     * Setup table column bindings
     */
    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("complaintId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDepartment.setCellValueFactory(new PropertyValueFactory<>("department"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
    }
    
    /**
     * Initialize user information and load data
     */
    public void initializeUser(String userType, Long userId, String userName) {
        this.userType = userType;
        this.userId = userId;
        this.userName = userName;
        lblWelcome.setText("Welcome, " + userName + " (" + userType + ")");
        
        // Load dashboard data
        loadDashboardData();
        
        System.out.println("Dashboard initialized for user: " + userName);
    }
    
    /**
     * Load all dashboard data (statistics and complaints)
     */
    private void loadDashboardData() {
        loadComplaintStatistics();
        loadRecentComplaints();
        updateLastUpdatedLabel();
    }
    
    /**
     * Load complaint statistics
     */
    private void loadComplaintStatistics() {
        EntityManager em = emf.createEntityManager();
        try {
            // Total complaints
            TypedQuery<Long> totalQuery = em.createQuery(
                "SELECT COUNT(c) FROM Complaint c WHERE c.citizen.citizen_id = :citizenId", 
                Long.class);
            totalQuery.setParameter("citizenId", userId);
            Long total = totalQuery.getSingleResult();
            lblTotalComplaints.setText(String.valueOf(total));
            
            // Pending complaints (UNREAD + READ)
            TypedQuery<Long> pendingQuery = em.createQuery(
                "SELECT COUNT(c) FROM Complaint c WHERE c.citizen.citizen_id = :citizenId " +
                "AND (c.status = :unread OR c.status = :read)", 
                Long.class);
            pendingQuery.setParameter("citizenId", userId);
            pendingQuery.setParameter("unread", ComplaintStatus.UNREAD);
            pendingQuery.setParameter("read", ComplaintStatus.READ);
            Long pending = pendingQuery.getSingleResult();
            lblPendingComplaints.setText(String.valueOf(pending));
            
            // In Progress complaints
            TypedQuery<Long> inProgressQuery = em.createQuery(
                "SELECT COUNT(c) FROM Complaint c WHERE c.citizen.citizen_id = :citizenId " +
                "AND c.status = :inProgress", 
                Long.class);
            inProgressQuery.setParameter("citizenId", userId);
            inProgressQuery.setParameter("inProgress", ComplaintStatus.IN_PROGRESS);
            Long inProgress = inProgressQuery.getSingleResult();
            lblInProgressComplaints.setText(String.valueOf(inProgress));
            
            // Resolved complaints (CLOSED)
            TypedQuery<Long> resolvedQuery = em.createQuery(
                "SELECT COUNT(c) FROM Complaint c WHERE c.citizen.citizen_id = :citizenId " +
                "AND c.status = :closed", 
                Long.class);
            resolvedQuery.setParameter("citizenId", userId);
            resolvedQuery.setParameter("closed", ComplaintStatus.CLOSED);
            Long resolved = resolvedQuery.getSingleResult();
            lblResolvedComplaints.setText(String.valueOf(resolved));
            
        } catch (Exception e) {
            System.err.println("Error loading complaint statistics: " + e.getMessage());
            e.printStackTrace();
            // Set default values on error
            lblTotalComplaints.setText("0");
            lblPendingComplaints.setText("0");
            lblInProgressComplaints.setText("0");
            lblResolvedComplaints.setText("0");
        } finally {
            em.close();
        }
    }
    
    /**
     * Load recent complaints into table
     */
    private void loadRecentComplaints() {
        EntityManager em = emf.createEntityManager();
        try {
            // Query recent complaints (last 10)
            TypedQuery<Complaint> query = em.createQuery(
                "SELECT c FROM Complaint c WHERE c.citizen.citizen_id = :citizenId " +
                "ORDER BY c.complaint_date DESC", 
                Complaint.class);
            query.setParameter("citizenId", userId);
            query.setMaxResults(10);
            
            List<Complaint> complaints = query.getResultList();
            
            // Convert to table rows
            ObservableList<ComplaintRow> rows = FXCollections.observableArrayList();
            
            for (Complaint complaint : complaints) {
                // Convert Date to LocalDate for formatting
                String formattedDate = "";
                if (complaint.getComplaint_date() != null) {
                    formattedDate = new java.text.SimpleDateFormat("MMM dd, yyyy")
                        .format(complaint.getComplaint_date());
                }
                
                ComplaintRow row = new ComplaintRow(
                    String.valueOf(complaint.getComplaint_id()),
                    complaint.getComplaint_title(),
                    complaint.getComplaint_type(),
                    complaint.getDepartment().getDept_name(),
                    complaint.getResolve_status().toString(),
                    formattedDate
                );
                rows.add(row);
            }
            
            tblRecentComplaints.setItems(rows);
            
            // Update table info
            lblTableInfo.setText("Showing " + rows.size() + " complaint" + (rows.size() != 1 ? "s" : ""));
            
        } catch (Exception e) {
            System.err.println("Error loading recent complaints: " + e.getMessage());
            e.printStackTrace();
            lblTableInfo.setText("Showing 0 complaints");
        } finally {
            em.close();
        }
    }
    
    /**
     * Update the "last updated" label
     */
    private void updateLastUpdatedLabel() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        lblLastUpdated.setText("Last updated: " + java.time.LocalTime.now().format(timeFormatter));
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
     * Handle Refresh button click
     */
    @FXML
    private void handleRefresh(ActionEvent event) {
        System.out.println("Refreshing dashboard data...");
        loadDashboardData();
    }
    
    /**
     * Handle Logout button click
     */
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Close EntityManagerFactory
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
            
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
    
    /**
     * Inner class to represent table rows
     */
    public static class ComplaintRow {
        private final String complaintId;
        private final String title;
        private final String type;
        private final String department;
        private final String status;
        private final String date;
        
        public ComplaintRow(String complaintId, String title, String type, 
                           String department, String status, String date) {
            this.complaintId = complaintId;
            this.title = title;
            this.type = type;
            this.department = department;
            this.status = status;
            this.date = date;
        }
        
        public String getComplaintId() { return complaintId; }
        public String getTitle() { return title; }
        public String getType() { return type; }
        public String getDepartment() { return department; }
        public String getStatus() { return status; }
        public String getDate() { return date; }
    }
}

