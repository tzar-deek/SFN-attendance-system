package com.skillsfusionnetwork.student_attendance_sys;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private Button togglePasswordBtn;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    @FXML private Hyperlink gotoRegisterLink;

    private boolean isPasswordVisible = false;

    @FXML
    public void initialize() {
        // Sync text between hidden password field and visible text field
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        // Toggle password visibility
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

        // Inside LoginController.java -> initialize()
        if (loginButton != null) {
            loginButton.setOnAction(event -> {
                String user = usernameField.getText();
                String pass = passwordField.isVisible() ? passwordField.getText() : visiblePasswordField.getText();

                if (user.isEmpty() || pass.isEmpty()) {
                    errorLabel.setText("Please enter both username and password.");
                } else {
                    errorLabel.setText("");
                    AppState.setCurrentUsername(user);
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    SceneManager.switchScene(stage, "dashboard.fxml");
                }
            });
        }

        // Navigate to Register page
        if (gotoRegisterLink != null) {
            gotoRegisterLink.setOnAction(event -> {
                Stage stage = (Stage) gotoRegisterLink.getScene().getWindow();
                SceneManager.switchScene(stage, "register.fxml");
            });
        }
    }
}
