package com.skillsfusionnetwork.student_attendance_sys;

import atlantafx.base.theme.CupertinoDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // 1. Set the AtlantaFX theme stylesheet
        Application.setUserAgentStylesheet(new CupertinoDark().getUserAgentStylesheet());

        // 2. Load your FXML / Scene with corrected arguments
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("intro-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 600);

        stage.setTitle("SFN Attendance System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}