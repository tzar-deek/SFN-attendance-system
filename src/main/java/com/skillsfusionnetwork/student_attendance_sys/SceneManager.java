package com.skillsfusionnetwork.student_attendance_sys;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SceneManager {

    public static void switchScene(Stage stage, String fxmlFileName) {
        try {
            // Try multiple strategies to find the FXML file safely
            URL fileUrl = SceneManager.class.getResource(fxmlFileName);

            if (fileUrl == null) {
                fileUrl = SceneManager.class.getResource("/com/skillsfusionnetwork/student_attendance_sys/" + fxmlFileName);
            }

            if (fileUrl == null) {
                fileUrl = SceneManager.class.getClassLoader().getResource("com/skillsfusionnetwork/student_attendance_sys/" + fxmlFileName);
            }

            if (fileUrl == null) {
                System.err.println("FATAL ERROR: Could not find FXML file anywhere: " + fxmlFileName);
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fileUrl);
            Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}