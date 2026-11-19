package com.skillsfusionnetwork.student_attendance_sys;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class RegisterController {
    @FXML private TextField usernameField;

    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private Button togglePasswordBtn;

    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField visibleConfirmField;
    @FXML private Button toggleConfirmBtn;

    @FXML private Button registerButton;
    @FXML private Label errorLabel;
    @FXML private Hyperlink gotoLoginLink;

    private boolean isPasswordVisible = false;
    private boolean isConfirmVisible = false;

    @FXML
    public void initialize() {
        // Enforce alphanumeric constraint on username field
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^[a-zA-Z0-9]*$")) {
                usernameField.setText(oldValue);
            }
        });

        // Sync text binding for Password field
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
        if (togglePasswordBtn != null) {
            togglePasswordBtn.setOnAction(event -> {
                isPasswordVisible = !isPasswordVisible;
                if (isPasswordVisible) {
                    visiblePasswordField.setVisible(true);
                    visiblePasswordField.setManaged(true);
                    passwordField.setVisible(false);
                    passwordField.setManaged(false);
                    togglePasswordBtn.setText("🔒");
                } else {
                    visiblePasswordField.setVisible(false);
                    visiblePasswordField.setManaged(false);
                    passwordField.setVisible(true);
                    passwordField.setManaged(true);
                    togglePasswordBtn.setText("👁");
                }
            });
        }

        // Sync text binding for Confirm Password field
        visibleConfirmField.textProperty().bindBidirectional(confirmPasswordField.textProperty());
        if (toggleConfirmBtn != null) {
            toggleConfirmBtn.setOnAction(event -> {
                isConfirmVisible = !isConfirmVisible;
                if (isConfirmVisible) {
                    visibleConfirmField.setVisible(true);
                    visibleConfirmField.setManaged(true);
                    confirmPasswordField.setVisible(false);
                    confirmPasswordField.setManaged(false);
                    toggleConfirmBtn.setText("🔒");
                } else {
                    visibleConfirmField.setVisible(false);
                    visibleConfirmField.setManaged(false);
                    confirmPasswordField.setVisible(true);
                    confirmPasswordField.setManaged(true);
                    toggleConfirmBtn.setText("👁");
                }
            });
        }

        // Handle registration validation
        registerButton.setOnAction(event -> {
            String user = usernameField.getText();
            String pass = passwordField.isVisible() ? passwordField.getText() : visiblePasswordField.getText();
            String confirmPass = confirmPasswordField.isVisible() ? confirmPasswordField.getText() : visibleConfirmField.getText();

            if (user.isEmpty() || pass.isEmpty() || confirmPass.isEmpty()) {
                errorLabel.setText("Please fill in all fields.");
            } else if (pass.length() < 8) {
                errorLabel.setText("Password must be at least 8 characters long.");
            } else if (!pass.equals(confirmPass)) {
                errorLabel.setText("Passwords do not match.");
            } else {
                errorLabel.setText("");
                // Successfully registered -> Route back to login page
                Stage stage = (Stage) registerButton.getScene().getWindow();
                SceneManager.switchScene(stage, "login.fxml");
            }
        });

        // Navigate back to Login page
        gotoLoginLink.setOnAction(event -> {
            Stage stage = (Stage) gotoLoginLink.getScene().getWindow();
            SceneManager.switchScene(stage, "login.fxml");
        });
    }
}
