package com.skillsfusionnetwork.student_attendance_sys;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

public class IntroController {

    @FXML private Button continueButton;
    @FXML private Button openSystemButton;
    @FXML private Hyperlink skipLink;
    @FXML private Button continueButton1;
    @FXML private ImageView fingerIcon;
    @FXML

    public void initialize() {
        // Page 1 -> Page 2 (intro-view1.fxml)
        if (continueButton != null && continueButton.getText().contains("Continue")) {
            continueButton.setOnAction(event -> {
                Stage stage = (Stage) continueButton.getScene().getWindow();
                SceneManager.switchScene(stage, "intro-view1.fxml");
            });
        }

        // Page 2 -> Page 3 (intro-view2.fxml)
        if (continueButton1 != null && continueButton1.getText().contains("Continue")) {
            continueButton1.setOnAction(event -> {
                Stage stage = (Stage) continueButton1.getScene().getWindow();
                SceneManager.switchScene(stage, "intro-view2.fxml");
            });
        }

        // Skip link from any intro page
        if (skipLink != null) {
            skipLink.setOnAction(event -> {
                Stage stage = (Stage) skipLink.getScene().getWindow();
                // SceneManager.switchScene(stage, "dashboard.fxml");
            });
        }
    }
}





