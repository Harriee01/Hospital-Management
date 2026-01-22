package com.hospital;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main Entry point for the Hospital Management System Application.
 */
public class Main extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Load FXML
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainLayout.fxml"));
        Parent root = fxmlLoader.load();
        
        scene = new Scene(root, 1000, 600);
        
        stage.setTitle("Hospital Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
