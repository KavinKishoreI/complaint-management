package com.complaintmanagement;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main application class for the Complaint Management System.
 * This JavaFX application provides a user interface for managing customer complaints.
 */
public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        // Create a simple welcome scene
        Label welcomeLabel = new Label("Welcome to Complaint Management System");
        VBox root = new VBox(welcomeLabel);
        Scene scene = new Scene(root, 400, 300);
        
        primaryStage.setTitle("Complaint Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        System.out.println("Starting Complaint Management System...");
        launch(args);
    }
}
