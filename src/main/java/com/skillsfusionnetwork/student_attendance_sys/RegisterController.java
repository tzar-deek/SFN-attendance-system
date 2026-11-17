package com.skillsfusionnetwork.student_attendance_sys;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button registerButton;
    @FXML private Label errorLabel;
    @FXML private Hyperlink gotoLoginLink;

    @FXML
    public void initialize() {
        // Enforce alphanumeric constraint on username field
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^[a-zA-Z0-9]*$")) {
                usernameField.setText(oldValue);
            }
        });

        registerButton.setOnAction(event -> {
            String user = usernameField.getText();
            String pass = passwordField.getText();
            String confirmPass = confirmPasswordField.getText();

            if (user.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                errorLabel.setText("Please fill in all fields.");
            } else if (!pass.equals(confirmPass)) {
                errorLabel.setText("Passwords do not match.");
            } else {
                // Save user details here, then route to login page
                Stage stage = (Stage) registerButton.getScene().getWindow();
                SceneManager.switchScene(stage, "login.fxml");
            }
        });

        gotoLoginLink.setOnAction(event -> {
            Stage stage = (Stage) gotoLoginLink.getScene().getWindow();
            SceneManager.switchScene(stage, "login.fxml");
        });
    }
}
