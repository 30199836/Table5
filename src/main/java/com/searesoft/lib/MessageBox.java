package com.searesoft.lib;

import javafx.scene.control.Alert;

/**
 * Simple Message box helper class
 */
public class MessageBox {
    public static void show(String message) {
        //No Header, quick message
        show(message, "Information", null);
    }

    public static void show(String message, String caption) {
        //No Header
        show(message, caption, null);
    }

    public static void show(String message, String caption, String header) {
        show(Alert.AlertType.INFORMATION, message, caption, header);
    }

    public static void show(Alert.AlertType type, String message, String caption, String header) {
        Alert alert = new Alert(type);
        alert.setTitle(caption);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
