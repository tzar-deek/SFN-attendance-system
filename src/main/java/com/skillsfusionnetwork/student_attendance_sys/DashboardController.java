package com.skillsfusionnetwork.student_attendance_sys;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class DashboardController {

    // Sidebar Controls
    @FXML private VBox sidebar;
    @FXML private Button toggleSidebarBtn;
    @FXML private VBox brandTextBox;
    @FXML private Label workspaceHeader;
    @FXML private Label managementHeader;
    @FXML private Button navOverviewBtn;
    @FXML private Button navProgramsBtn;
    @FXML private Label sidebarProgramBadge;
    @FXML private Button navReportsBtn;
    @FXML private Button navSettingsBtn;
    @FXML private VBox scannerStatusBox;
    @FXML private Circle sidebarStatusCircle;
    @FXML private Label sidebarStatusLabel;
    @FXML private Label sidebarAvatarLabel;
    @FXML private VBox sidebarUserBox;
    @FXML private Label sidebarUsernameLabel;
    @FXML private Button logoutBtn;

    // Top Header & Greeting
    @FXML private Button openParticipantModalBtn;
    @FXML private Button openProgramModalBtn;
    @FXML private Label topAvatarLabel;
    @FXML private Label greetingLabel;

    // Pages
    @FXML private ScrollPane overviewPage;
    @FXML private ScrollPane programsPage;
    @FXML private VBox reportsPage;
    @FXML private VBox settingsPage;

    // Overview Metrics & Lists
    @FXML private Label programCountLabel;
    @FXML private Label participantCountLabel;
    @FXML private Hyperlink inlineRegisterProgramLink;
    @FXML private VBox emptyProgramsBox;
    @FXML private VBox overviewProgramsList;
    @FXML private VBox allProgramsList;
    @FXML private Button programsPageAddBtn;

    // Program Modal Controls
    @FXML private StackPane programModalOverlay;
    @FXML private Button closeProgramModalBtn;
    @FXML private Button cancelProgramModalBtn;
    @FXML private TextField programNameInput;
    @FXML private TextField programVenueInput;
    @FXML private DatePicker programStartDate;
    @FXML private DatePicker programEndDate;
    @FXML private TextArea programDescInput;
    @FXML private Label programModalError;
    @FXML private Button submitProgramBtn;

    // Participant Modal Controls & Fingerprint UI
    @FXML private StackPane participantModalOverlay;
    @FXML private Button closeParticipantModalBtn;
    @FXML private Button cancelParticipantModalBtn;
    @FXML private TextField participantNameInput;
    @FXML private ComboBox<AppState.Program> participantProgramCombo;
    @FXML private TextField participantPhoneInput;

    @FXML private VBox leftThumbCard;
    @FXML private Label leftThumbStatus;
    @FXML private VBox leftIndexCard;
    @FXML private Label leftIndexStatus;
    @FXML private VBox rightThumbCard;
    @FXML private Label rightThumbStatus;
    @FXML private VBox rightIndexCard;
    @FXML private Label rightIndexStatus;

    @FXML private HBox modalScannerBar;
    @FXML private Circle modalStatusCircle;
    @FXML private Label modalStatusTitle;
    @FXML private Label scannerCountLabel;
    @FXML private Label modalConnectedTag;

    @FXML private Label participantModalError;
    @FXML private Button submitParticipantBtn;

    private boolean isSidebarCollapsed = false;
    private final boolean[] currentFingerprints = new boolean[4]; // [Left Thumb, Left Index, Right Thumb, Right Index]

    @FXML
    public void initialize() {
        setupUserIdentity();
        setupSidebarNavigation();
        setupScannerStatusBinding();
        setupModals();
        refreshDashboardUI();
    }

    private void setupUserIdentity() {
        String username = AppState.getCurrentUsername();
        String initials = username.length() >= 2
                ? username.substring(0, 2).toUpperCase()
                : username.toUpperCase();

        greetingLabel.setText("Good morning, " + username);
        sidebarUsernameLabel.setText(username);
        sidebarAvatarLabel.setText(initials);
        topAvatarLabel.setText(initials);
    }

    private void setupSidebarNavigation() {
        toggleSidebarBtn.setOnAction(e -> {
            isSidebarCollapsed = !isSidebarCollapsed;
            sidebar.setPrefWidth(isSidebarCollapsed ? 76.0 : 240.0);

            setNodeVisibility(brandTextBox, !isSidebarCollapsed);
            setNodeVisibility(workspaceHeader, !isSidebarCollapsed);
            setNodeVisibility(managementHeader, !isSidebarCollapsed);
            setNodeVisibility(sidebarProgramBadge, !isSidebarCollapsed);
            setNodeVisibility(scannerStatusBox, !isSidebarCollapsed);
            setNodeVisibility(sidebarUserBox, !isSidebarCollapsed);

            navOverviewBtn.setText(isSidebarCollapsed ? "⊞" : "⊞  Overview");
            navProgramsBtn.setText(isSidebarCollapsed ? "👥" : "👥  Programs");
            navReportsBtn.setText(isSidebarCollapsed ? "📊" : "📊  Reports");
            navSettingsBtn.setText(isSidebarCollapsed ? "⚙" : "⚙  Settings");
        });

        navOverviewBtn.setOnAction(e -> switchTab(overviewPage, navOverviewBtn));
        navProgramsBtn.setOnAction(e -> switchTab(programsPage, navProgramsBtn));
        navReportsBtn.setOnAction(e -> switchTab(reportsPage, navReportsBtn));
        navSettingsBtn.setOnAction(e -> switchTab(settingsPage, navSettingsBtn));

        logoutBtn.setOnAction(e -> {
            AppState.clearSession();
            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            SceneManager.switchScene(stage, "login.fxml");
        });
    }

    private void setupScannerStatusBinding() {
        // Clicking sidebar scanner widget toggles connection state for testing
        scannerStatusBox.setOnMouseClicked(e -> {
            boolean newState = !AppState.isScannerConnected();
            AppState.setScannerConnected(newState);
        });

        AppState.scannerConnectedProperty().addListener((obs, oldVal, isConnected) -> {
            updateScannerUI(isConnected);
        });

        updateScannerUI(AppState.isScannerConnected());
    }

    private void updateScannerUI(boolean isConnected) {
        if (isConnected) {
            sidebarStatusCircle.setFill(javafx.scene.paint.Color.web("#22c55e"));
            sidebarStatusLabel.setText("Connected");

            modalStatusCircle.setFill(javafx.scene.paint.Color.web("#22c55e"));
            modalStatusTitle.setText("ZKTeco 9500 ready");
            modalConnectedTag.setText("CONNECTED");
            modalConnectedTag.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
        } else {
            sidebarStatusCircle.setFill(javafx.scene.paint.Color.web("#ef4444"));
            sidebarStatusLabel.setText("Disconnected");

            modalStatusCircle.setFill(javafx.scene.paint.Color.web("#ef4444"));
            modalStatusTitle.setText("ZKTeco 9500 offline");
            modalConnectedTag.setText("DISCONNECTED");
            modalConnectedTag.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        }
    }

    private void switchTab(Node targetPage, Button activeBtn) {
        setNodeVisibility(overviewPage, targetPage == overviewPage);
        setNodeVisibility(programsPage, targetPage == programsPage);
        setNodeVisibility(reportsPage, targetPage == reportsPage);
        setNodeVisibility(settingsPage, targetPage == settingsPage);

        navOverviewBtn.getStyleClass().remove("nav-btn-active");
        navProgramsBtn.getStyleClass().remove("nav-btn-active");
        navReportsBtn.getStyleClass().remove("nav-btn-active");
        navSettingsBtn.getStyleClass().remove("nav-btn-active");

        if (!activeBtn.getStyleClass().contains("nav-btn-active")) {
            activeBtn.getStyleClass().add("nav-btn-active");
        }
    }

    private void setupModals() {
        openProgramModalBtn.setOnAction(e -> openProgramModal());
        inlineRegisterProgramLink.setOnAction(e -> openProgramModal());
        programsPageAddBtn.setOnAction(e -> openProgramModal());

        closeProgramModalBtn.setOnAction(e -> setNodeVisibility(programModalOverlay, false));
        cancelProgramModalBtn.setOnAction(e -> setNodeVisibility(programModalOverlay, false));

        submitProgramBtn.setOnAction(e -> {
            String name = programNameInput.getText().trim();
            String venue = programVenueInput.getText().trim();
            String desc = programDescInput.getText().trim();

            if (name.isEmpty() || venue.isEmpty()) {
                programModalError.setText("Program name and venue are required.");
                return;
            }

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            String start = programStartDate.getValue() != null ? programStartDate.getValue().format(fmt) : "TBD";
            String end = programEndDate.getValue() != null ? programEndDate.getValue().format(fmt) : "TBD";

            AppState.getPrograms().add(new AppState.Program(name, venue, start, end, desc));
            setNodeVisibility(programModalOverlay, false);
            refreshDashboardUI();
        });

        openParticipantModalBtn.setOnAction(e -> openParticipantModal());
        closeParticipantModalBtn.setOnAction(e -> setNodeVisibility(participantModalOverlay, false));
        cancelParticipantModalBtn.setOnAction(e -> setNodeVisibility(participantModalOverlay, false));

        // Fingerprint Capture (Requires scanner connection)
        leftThumbCard.setOnMouseClicked(e -> { if (AppState.isScannerConnected()) toggleFingerprintScan(0, leftThumbCard, leftThumbStatus); });
        leftIndexCard.setOnMouseClicked(e -> { if (AppState.isScannerConnected()) toggleFingerprintScan(1, leftIndexCard, leftIndexStatus); });
        rightThumbCard.setOnMouseClicked(e -> { if (AppState.isScannerConnected()) toggleFingerprintScan(2, rightThumbCard, rightThumbStatus); });
        rightIndexCard.setOnMouseClicked(e -> { if (AppState.isScannerConnected()) toggleFingerprintScan(3, rightIndexCard, rightIndexStatus); });

        submitParticipantBtn.setOnAction(e -> {
            String fullName = participantNameInput.getText().trim();
            AppState.Program selectedProgram = participantProgramCombo.getValue();
            String phone = participantPhoneInput.getText().trim();

            int capturedCount = 0;
            for (boolean b : currentFingerprints) if (b) capturedCount++;

            if (fullName.isEmpty() || selectedProgram == null) {
                participantModalError.setText("Please enter participant name and select a program.");
            } else if (!AppState.isScannerConnected()) {
                participantModalError.setText("Cannot enroll: ZKTeco 9500 scanner is disconnected.");
            } else if (capturedCount < 4) {
                participantModalError.setText("Please capture all 4 fingerprints using the scanner.");
            } else {
                AppState.getParticipants().add(new AppState.Participant(fullName, selectedProgram.getName(), phone, currentFingerprints.clone()));
                setNodeVisibility(participantModalOverlay, false);
                refreshDashboardUI();
            }
        });
    }

    private void toggleFingerprintScan(int index, VBox card, Label statusLabel) {
        currentFingerprints[index] = !currentFingerprints[index];
        if (currentFingerprints[index]) {
            card.getStyleClass().add("finger-card-captured");
            statusLabel.setText("Captured ✓");
        } else {
            card.getStyleClass().remove("finger-card-captured");
            statusLabel.setText("Tap to capture");
        }

        int count = 0;
        for (boolean b : currentFingerprints) if (b) count++;
        scannerCountLabel.setText(count + " of 4 fingerprints captured");
    }

    private void openProgramModal() {
        programNameInput.clear();
        programVenueInput.clear();
        programStartDate.setValue(null);
        programEndDate.setValue(null);
        programDescInput.clear();
        programModalError.setText("");
        setNodeVisibility(programModalOverlay, true);
    }

    private void openParticipantModal() {
        participantNameInput.clear();
        participantPhoneInput.clear();
        participantModalError.setText("");

        for (int i = 0; i < 4; i++) currentFingerprints[i] = false;
        resetFingerCardUI(leftThumbCard, leftThumbStatus);
        resetFingerCardUI(leftIndexCard, leftIndexStatus);
        resetFingerCardUI(rightThumbCard, rightThumbStatus);
        resetFingerCardUI(rightIndexCard, rightIndexStatus);
        scannerCountLabel.setText("0 of 4 fingerprints captured");

        participantProgramCombo.setItems(AppState.getPrograms());

        // Custom cell factory so program names render correctly in dropdown
        participantProgramCombo.setCellFactory(param -> new ListCell<AppState.Program>() {
            @Override
            protected void updateItem(AppState.Program program, boolean empty) {
                super.updateItem(program, empty);
                setText(empty || program == null ? null : program.getName());
            }
        });

        participantProgramCombo.setButtonCell(new ListCell<AppState.Program>() {
            @Override
            protected void updateItem(AppState.Program program, boolean empty) {
                super.updateItem(program, empty);
                setText(empty || program == null ? "Select a program" : program.getName());
            }
        });

        if (!AppState.getPrograms().isEmpty()) {
            participantProgramCombo.setValue(AppState.getPrograms().get(0));
        }

        setNodeVisibility(participantModalOverlay, true);
    }

    private void resetFingerCardUI(VBox card, Label status) {
        card.getStyleClass().remove("finger-card-captured");
        status.setText("Tap to capture");
    }

    private void refreshDashboardUI() {
        int progCount = AppState.getPrograms().size();
        int partCount = AppState.getParticipants().size();

        programCountLabel.setText(String.valueOf(progCount));
        participantCountLabel.setText(String.valueOf(partCount));
        sidebarProgramBadge.setText(String.valueOf(progCount));

        overviewProgramsList.getChildren().clear();
        allProgramsList.getChildren().clear();

        if (progCount == 0) {
            setNodeVisibility(emptyProgramsBox, true);
            setNodeVisibility(overviewProgramsList, false);

            Label emptyDirLabel = new Label("No programs registered yet. Click '+ Register program' to add one.");
            emptyDirLabel.getStyleClass().add("muted-subtitle");
            allProgramsList.getChildren().add(emptyDirLabel);
        } else {
            setNodeVisibility(emptyProgramsBox, false);
            setNodeVisibility(overviewProgramsList, true);

            for (AppState.Program prog : AppState.getPrograms()) {
                overviewProgramsList.getChildren().add(createProgramRowCard(prog));
                allProgramsList.getChildren().add(createProgramRowCard(prog));
            }
        }
    }

    private HBox createProgramRowCard(AppState.Program program) {
        HBox card = new HBox(16.0);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("program-row-card");
        card.setPadding(new Insets(16.0, 20.0, 16.0, 20.0));

        String shortTag = program.getName().length() >= 3
                ? program.getName().substring(0, 3).toUpperCase()
                : program.getName().toUpperCase();
        Label tagLabel = new Label(shortTag);
        tagLabel.getStyleClass().add("program-tag-badge");

        VBox infoBox = new VBox(4.0);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        Label nameLabel = new Label(program.getName());
        nameLabel.getStyleClass().add("program-row-title");
        Label metaLabel = new Label(program.getStartDate() + " - " + program.getEndDate() + "  •  " + program.getVenue());
        metaLabel.getStyleClass().add("muted-subtitle");
        infoBox.getChildren().addAll(nameLabel, metaLabel);

        VBox countBox = new VBox(2.0);
        countBox.setAlignment(Pos.CENTER_RIGHT);
        Label countNum = new Label(String.valueOf(program.getParticipantCount()));
        countNum.getStyleClass().add("program-count-num");
        Label countText = new Label("participants");
        countText.getStyleClass().add("muted-subtitle");
        countBox.getChildren().addAll(countNum, countText);

        Button deleteBtn = new Button("Delete");
        deleteBtn.getStyleClass().add("btn-secondary");
        deleteBtn.setOnAction(e -> {
            AppState.removeProgram(program);
            refreshDashboardUI();
        });

        card.getChildren().addAll(tagLabel, infoBox, countBox, deleteBtn);
        return card;
    }

    private void setNodeVisibility(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }
}