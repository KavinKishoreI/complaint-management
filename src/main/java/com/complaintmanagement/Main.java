package com.complaintmanagement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class for the Complaint Management System.
 * This JavaFX application provides a user interface for managing customer complaints.
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the Dashboard FXML file with its controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Parent root = loader.load();
            
            // Create scene with the loaded FXML
            Scene scene = new Scene(root, 800, 600);
            
            // Configure the primary stage
            primaryStage.setTitle("Complaint Management System");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            System.out.println("Complaint Management System started with FXML UI");
        } catch (Exception e) {
            System.err.println("Error loading Dashboard FXML:");
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
