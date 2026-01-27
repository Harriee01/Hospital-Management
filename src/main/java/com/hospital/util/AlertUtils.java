package com.hospital.util;

import javafx.scene.control.Alert;

public class AlertUtils {
    public static void showAlert(String title, String content) {
        Alert.AlertType alertType = title.equals("Success") ? Alert.AlertType.INFORMATION
                : title.equals("Error") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION;
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
