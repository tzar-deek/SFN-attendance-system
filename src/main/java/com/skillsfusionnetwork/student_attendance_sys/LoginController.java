package com.skillsfusionnetwork.student_attendance_sys;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    @FXML private Hyperlink gotoRegisterLink;

    @FXML
    public void initialize() {
        // Handle login button action
        if (loginButton != null) {
            loginButton.setOnAction(event -> {
                // Add your login logic here
            });
        }

        // Handle clicking "Create an account" hyperlink
        if (gotoRegisterLink != null) {
            gotoRegisterLink.setOnAction(event -> {
                Stage stage = (Stage) gotoRegisterLink.getScene().getWindow();
                SceneManager.switchScene(stage, "register.fxml");
            });
        }
    }
}
