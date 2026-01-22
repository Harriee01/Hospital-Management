package com.hospital;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main Entry point for the Hospital Management System Application.
 * 
 * This class extends JavaFX Application and initializes the primary stage
 * with the main UI layout loaded from MainLayout.fxml.
 * Sets up the application window with title and initial scene dimensions.
 */
public class Main extends Application {

    private static Scene scene;

    /**
     * Initializes and displays the JavaFX application window.
     * Loads the FXML layout file and creates the main scene.
     * 
     * @param stage The primary stage (window) for the application
     * @throws IOException if FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Load FXML layout file from resources
        // This loads the MainLayout.fxml which defines the UI structure
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainLayout.fxml"));
        Parent root = fxmlLoader.load();
        
        // Create scene with specified dimensions (width: 1000px, height: 600px)
        scene = new Scene(root, 1000, 600);
        
        // Configure and display the application window
        stage.setTitle("Hospital Management System");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Main method that launches the JavaFX application.
     * This is the entry point when running the application.
     * 
     * @param args Command-line arguments (not used in this application)
     */
    public static void main(String[] args) {
        launch(); // Starts the JavaFX application lifecycle
    }
}
