package com.skillsfusionnetwork.student_attendance_sys;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        loginButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Simple validation logic (Replace this with database/API checks later)
            if (username.equals("admin") && password.equals("1234")) {
                Stage stage = (Stage) loginButton.getScene().getWindow();
                // Switch to your main dashboard FXML view when login is successful
                // SceneManager.switchScene(stage, "dashboard.fxml");
                errorLabel.setText("Login successful! Loading dashboard...");
            } else {
                errorLabel.setText("Invalid username or password.");
            }
        });
    }
}
