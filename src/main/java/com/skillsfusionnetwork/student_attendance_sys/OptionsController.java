package com.skillsfusionnetwork.student_attendance_sys;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OptionsController {
    @FXML private VBox createAccountCard;
    @FXML private VBox loginCard;

    @FXML
    public void initialize() {
        createAccountCard.setOnMouseClicked(event -> {
            Stage stage = (Stage) createAccountCard.getScene().getWindow();
            SceneManager.switchScene(stage, "register.fxml");
        });

        loginCard.setOnMouseClicked(event -> {
            Stage stage = (Stage) loginCard.getScene().getWindow();
            SceneManager.switchScene(stage, "login.fxml");
        });
    }
}
