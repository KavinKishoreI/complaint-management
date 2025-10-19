package com.complaintmanagement.controller;

import com.complaintmanagement.model.Authority;
import com.complaintmanagement.model.Complaint;
import com.complaintmanagement.model.ComplaintStatus;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for the Authority Dashboard
 */
public class AuthorityDashboardController {

    @FXML
    private Label lblWelcome;

    @FXML
    private Label lblTotalComplaints;

    @FXML
    private Label lblUnreadComplaints;

    @FXML
    private Label lblInProgressComplaints;

    @FXML
    private Label lblResolvedComplaints;

    @FXML
    private Label lblDepartment;

    @FXML
    private Label lblTableInfo;

    @FXML
    private Label lblLastUpdated;

    @FXML
    private Button btnLogout;

    @FXML
    private Button btnRefresh;

    @FXML
    private TableView<ComplaintRow> tblComplaints;

    @FXML
    private TableColumn<ComplaintRow, String> colId;

    @FXML
    private TableColumn<ComplaintRow, String> colTitle;

    @FXML
    private TableColumn<ComplaintRow, String> colType;

    @FXML
    private TableColumn<ComplaintRow, String> colCitizen;

    @FXML
    private TableColumn<ComplaintRow, String> colStatus;

    @FXML
    private TableColumn<ComplaintRow, String> colDate;

    @FXML
    private TableColumn<ComplaintRow, Void> colAction;

    // Authority information
    private Long authorityId;
    private String authorityUsername;
    private Authority currentAuthority;

    private EntityManagerFactory emf;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        System.out.println("AuthorityDashboardController initialized");

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
        colCitizen.setCellValueFactory(new PropertyValueFactory<>("citizen"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        // Add Respond/View button in Action column
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button actionButton = new Button("Respond");

            {
                actionButton.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #e74c3c, #c0392b);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 5 15;" +
                    "-fx-background-radius: 5;" +
                    "-fx-cursor: hand;"
                );

                actionButton.setOnMouseEntered(e -> actionButton.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #c0392b, #a93226);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 5 15;" +
                    "-fx-background-radius: 5;" +
                    "-fx-cursor: hand;"
                ));

                actionButton.setOnMouseExited(e -> actionButton.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #e74c3c, #c0392b);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;" +
                    "-fx-padding: 5 15;" +
                    "-fx-background-radius: 5;" +
                    "-fx-cursor: hand;"
                ));

                actionButton.setOnAction(event -> {
                    ComplaintRow row = getTableView().getItems().get(getIndex());
                    openComplaintResponse(row.getComplaintId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButton);
                }
            }
        });
    }

    /**
     * Initialize authority information and load data
     */
    public void initializeAuthority(Long authorityId, String authorityUsername) {
        this.authorityId = authorityId;
        this.authorityUsername = authorityUsername;

        EntityManager em = emf.createEntityManager();
        try {
            // Load authority entity
            currentAuthority = em.find(Authority.class, authorityId);

            if (currentAuthority != null) {
                lblWelcome.setText("Welcome, " + authorityUsername);
                lblDepartment.setText(currentAuthority.getDepartment().getDept_name());

                System.out.println("Authority initialized: " + authorityUsername + 
                                 " - Department: " + currentAuthority.getDepartment().getDept_name());
            }
        } catch (Exception e) {
            System.err.println("Error loading authority: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }

        // Load dashboard data
        loadDashboardData();
    }

    /**
     * Load all dashboard data
     */
    private void loadDashboardData() {
        loadComplaintStatistics();
        loadAssignedComplaints();
        updateLastUpdatedLabel();
    }

    /**
     * Load complaint statistics for the department
     */
    private void loadComplaintStatistics() {
        EntityManager em = emf.createEntityManager();
        try {
            if (currentAuthority == null) {
                return;
            }

            Long deptId = currentAuthority.getDepartment().getDept_id();

            // Total complaints assigned to department
            TypedQuery<Long> totalQuery = em.createQuery(
                "SELECT COUNT(c) FROM Complaint c WHERE c.department.dept_id = :deptId",
                Long.class);
            totalQuery.setParameter("deptId", deptId);
            Long total = totalQuery.getSingleResult();
            lblTotalComplaints.setText(String.valueOf(total));

            // Unread complaints
            TypedQuery<Long> unreadQuery = em.createQuery(
                "SELECT COUNT(c) FROM Complaint c WHERE c.department.dept_id = :deptId " +
                "AND c.resolve_status = :status",
                Long.class);
            unreadQuery.setParameter("deptId", deptId);
            unreadQuery.setParameter("status", ComplaintStatus.UNREAD);
            Long unread = unreadQuery.getSingleResult();
            lblUnreadComplaints.setText(String.valueOf(unread));

            // In Progress complaints
            TypedQuery<Long> inProgressQuery = em.createQuery(
                "SELECT COUNT(c) FROM Complaint c WHERE c.department.dept_id = :deptId " +
                "AND c.resolve_status = :status",
                Long.class);
            inProgressQuery.setParameter("deptId", deptId);
            inProgressQuery.setParameter("status", ComplaintStatus.IN_PROGRESS);
            Long inProgress = inProgressQuery.getSingleResult();
            lblInProgressComplaints.setText(String.valueOf(inProgress));

            // Resolved complaints
            TypedQuery<Long> resolvedQuery = em.createQuery(
                "SELECT COUNT(c) FROM Complaint c WHERE c.department.dept_id = :deptId " +
                "AND c.resolve_status = :status",
                Long.class);
            resolvedQuery.setParameter("deptId", deptId);
            resolvedQuery.setParameter("status", ComplaintStatus.CLOSED);
            Long resolved = resolvedQuery.getSingleResult();
            lblResolvedComplaints.setText(String.valueOf(resolved));

        } catch (Exception e) {
            System.err.println("Error loading complaint statistics: " + e.getMessage());
            e.printStackTrace();
            lblTotalComplaints.setText("0");
            lblUnreadComplaints.setText("0");
            lblInProgressComplaints.setText("0");
            lblResolvedComplaints.setText("0");
        } finally {
            em.close();
        }
    }

    /**
     * Load complaints assigned to this authority's department
     */
    private void loadAssignedComplaints() {
        EntityManager em = emf.createEntityManager();
        try {
            if (currentAuthority == null) {
                return;
            }

            Long deptId = currentAuthority.getDepartment().getDept_id();

            // Query all complaints for this department, ordered by status and date
            TypedQuery<Complaint> query = em.createQuery(
                "SELECT c FROM Complaint c WHERE c.department.dept_id = :deptId " +
                "ORDER BY CASE c.resolve_status " +
                "  WHEN com.complaintmanagement.model.ComplaintStatus.UNREAD THEN 1 " +
                "  WHEN com.complaintmanagement.model.ComplaintStatus.READ THEN 2 " +
                "  WHEN com.complaintmanagement.model.ComplaintStatus.IN_PROGRESS THEN 3 " +
                "  WHEN com.complaintmanagement.model.ComplaintStatus.CLOSED THEN 4 " +
                "END, c.complaint_date DESC",
                Complaint.class);
            query.setParameter("deptId", deptId);

            List<Complaint> complaints = query.getResultList();

            // Convert to table rows
            ObservableList<ComplaintRow> rows = FXCollections.observableArrayList();

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

            for (Complaint complaint : complaints) {
                String formattedDate = dateFormat.format(complaint.getComplaint_date());

                ComplaintRow row = new ComplaintRow(
                    String.valueOf(complaint.getComplaint_id()),
                    complaint.getComplaint_title(),
                    complaint.getComplaint_type(),
                    complaint.getCitizen().getCitizen_name(),
                    complaint.getResolve_status().getDisplayName(),
                    formattedDate
                );
                rows.add(row);
            }

            tblComplaints.setItems(rows);

            // Update table info
            lblTableInfo.setText("Showing " + rows.size() + " complaint" + (rows.size() != 1 ? "s" : ""));

        } catch (Exception e) {
            System.err.println("Error loading assigned complaints: " + e.getMessage());
            e.printStackTrace();
            lblTableInfo.setText("Showing 0 complaints");
        } finally {
            em.close();
        }
    }

    /**
     * Update the last updated label
     */
    private void updateLastUpdatedLabel() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        lblLastUpdated.setText("Last updated: " + java.time.LocalTime.now().format(timeFormatter));
    }

    /**
     * Open complaint response page
     */
    private void openComplaintResponse(String complaintIdStr) {
        try {
            Long complaintId = Long.parseLong(complaintIdStr);
            System.out.println("Opening response page for complaint: " + complaintId);
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AuthorityRespondComplaint.fxml"));
            Parent root = loader.load();
            
            // Pass data to response controller
            AuthorityRespondController controller = loader.getController();
            controller.initializeData(authorityId, authorityUsername, complaintId);
            
            // Get current stage and set new scene
            Stage stage = (Stage) tblComplaints.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/authority-respond.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Respond to Complaint");
            
            System.out.println("Navigated to response page");
            
        } catch (Exception e) {
            System.err.println("Error opening response page: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Navigation Error");
            alert.setContentText("Could not open response page. Please try again.");
            alert.showAndWait();
        }
    }

    /**
     * Handle Refresh button
     */
    @FXML
    private void handleRefresh(ActionEvent event) {
        System.out.println("Refreshing dashboard data...");
        loadDashboardData();
    }

    /**
     * Handle Logout button
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

            System.out.println("Authority logged out");

        } catch (Exception e) {
            System.err.println("Error logging out: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cleanup method
     */
    public void cleanup() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    /**
     * Inner class for table rows
     */
    public static class ComplaintRow {
        private final String complaintId;
        private final String title;
        private final String type;
        private final String citizen;
        private final String status;
        private final String date;

        public ComplaintRow(String complaintId, String title, String type, 
                          String citizen, String status, String date) {
            this.complaintId = complaintId;
            this.title = title;
            this.type = type;
            this.citizen = citizen;
            this.status = status;
            this.date = date;
        }

        public String getComplaintId() { return complaintId; }
        public String getTitle() { return title; }
        public String getType() { return type; }
        public String getCitizen() { return citizen; }
        public String getStatus() { return status; }
        public String getDate() { return date; }
    }
}
