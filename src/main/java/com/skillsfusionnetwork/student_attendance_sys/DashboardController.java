package com.skillsfusionnetwork.student_attendance_sys;

import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class DashboardController {

    // Sidebar Controls
    @FXML private VBox sidebar;
    @FXML private Button toggleSidebarBtn;
    @FXML private HBox brandTextBox;
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
    @FXML private ScrollPane reportsPage;
    @FXML private ScrollPane settingsPage;

    // Overview Metrics & Lists
    @FXML private Label programCountLabel;
    @FXML private Label participantCountLabel;
    @FXML private Hyperlink inlineRegisterProgramLink;
    @FXML private VBox emptyProgramsBox;
    @FXML private VBox overviewProgramsList;
    @FXML private VBox allProgramsList;
    @FXML private Button programsPageAddBtn;

    // Reports Page Controls
    @FXML private ComboBox<String> reportProgramFilterCombo;
    @FXML private Button exportReportBtn;
    @FXML private Label reportTotalParticipantsLabel;
    @FXML private Label reportBiometricCompleteLabel;
    @FXML private Label reportActiveProgramsLabel;
    @FXML private Label reportStatusMessage;
    @FXML private TableView<AppState.Participant> attendanceReportTable;
    @FXML private TableColumn<AppState.Participant, String> colParticipantName;
    @FXML private TableColumn<AppState.Participant, String> colParticipantProgram;
    @FXML private TableColumn<AppState.Participant, String> colParticipantPhone;
    @FXML private TableColumn<AppState.Participant, String> colFingerprintsEnrolled;
    @FXML private TableColumn<AppState.Participant, String> colBiometricStatus;

    // Settings Page Controls
    @FXML private ToggleButton settingsScannerToggle;
    @FXML private Button settingsToggleSidebarBtn;
    @FXML private Label settingsStoragePathLabel;
    @FXML private Button settingsRefreshDataBtn;
    @FXML private Button settingsClearDataBtn;
    @FXML private Label settingsFeedbackLabel;

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
    @FXML private ImageView leftThumbImg;
    @FXML private Label leftThumbStatus;

    @FXML private VBox leftIndexCard;
    @FXML private ImageView leftIndexImg;
    @FXML private Label leftIndexStatus;

    @FXML private VBox rightThumbCard;
    @FXML private ImageView rightThumbImg;
    @FXML private Label rightThumbStatus;

    @FXML private VBox rightIndexCard;
    @FXML private ImageView rightIndexImg;
    @FXML private Label rightIndexStatus;

    // Scan Pop-up Controls
    @FXML private StackPane fingerprintScanPopupOverlay;
    @FXML private Label scanPopupFingerTitle;
    @FXML private Label scanPopupStatusLabel;
    @FXML private ImageView scanPopupImageView;
    @FXML private Button cancelScanPopupBtn;
    @FXML private Button confirmScanPopupBtn;

    @FXML private HBox modalScannerBar;
    @FXML private Circle modalStatusCircle;
    @FXML private Label modalStatusTitle;
    @FXML private Label scannerCountLabel;
    @FXML private Label modalConnectedTag;

    @FXML private Label participantModalError;
    @FXML private Button submitParticipantBtn;

    private static final String[] FINGER_TITLES = {
            "Left hand Thumb",
            "Left hand Index",
            "Right hand Thumb",
            "Right hand Index"
    };

    private Image blackFingerprintImage;
    private Image greenFingerprintImage;

    private boolean isSidebarCollapsed = false;
    private boolean isRefreshingReports = false;
    private long lastGestureToggleMs = 0L;
    private int activeScanIndex = -1;
    private boolean activeScanCompleted = false;
    private PauseTransition activeScanTimer;
    private final boolean[] currentFingerprints = new boolean[4];

    @FXML
    public void initialize() {
        loadDatabaseState();
        loadFingerprintImages();
        setupUserIdentity();
        setupSidebarNavigation();
        setupMousepadGesture();
        setupScannerStatusBinding();
        setupProgramComboBox();
        setupScanPopup();
        setupModals();
        setupReportsPage();
        setupSettingsPage();
        refreshDashboardUI();
    }

    private void loadDatabaseState() {
        DatabaseManager.initializeDatabase();
        AppState.getPrograms().setAll(DatabaseManager.loadAllPrograms());
        AppState.getParticipants().setAll(DatabaseManager.loadAllParticipants());
    }

    private void loadFingerprintImages() {
        blackFingerprintImage = new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("images/blackFingerPrint.png"))
        );
        greenFingerprintImage = new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("images/greenFingerPrint.png"))
        );
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
        toggleSidebarBtn.setOnAction(e -> setSidebarCollapsed(!isSidebarCollapsed));

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

    private void setSidebarCollapsed(boolean collapsed) {
        isSidebarCollapsed = collapsed;
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
    }

    private void setupMousepadGesture() {
        sidebar.addEventFilter(ScrollEvent.SCROLL, event -> {
            long now = System.currentTimeMillis();
            if (now - lastGestureToggleMs < 350) {
                return;
            }

            double dx = event.getDeltaX();
            double dy = event.getDeltaY();

            if (dx < -25 && !isSidebarCollapsed) {
                lastGestureToggleMs = now;
                setSidebarCollapsed(true);
                event.consume();
            } else if (dx > 25 && isSidebarCollapsed) {
                lastGestureToggleMs = now;
                setSidebarCollapsed(false);
                event.consume();
            } else if (Math.abs(dy) > 35 && Math.abs(dx) < 10) {
                lastGestureToggleMs = now;
                setSidebarCollapsed(!isSidebarCollapsed);
                event.consume();
            }
        });
    }

    private void setupScannerStatusBinding() {
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

        if (settingsScannerToggle != null) {
            settingsScannerToggle.setSelected(isConnected);
            settingsScannerToggle.setText(isConnected ? "Connected" : "Disconnected");
        }
    }

    private void setupProgramComboBox() {
        participantProgramCombo.setItems(AppState.getPrograms());

        participantProgramCombo.setConverter(new StringConverter<AppState.Program>() {
            @Override
            public String toString(AppState.Program program) {
                return program == null ? "" : program.getName();
            }

            @Override
            public AppState.Program fromString(String string) {
                if (string == null || string.isBlank()) return null;
                return AppState.getPrograms().stream()
                        .filter(p -> p.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        participantProgramCombo.setCellFactory(param -> new ListCell<AppState.Program>() {
            @Override
            protected void updateItem(AppState.Program program, boolean empty) {
                super.updateItem(program, empty);
                setText(empty || program == null ? null : program.getName());
                setStyle("-fx-text-fill: #0f172a;");
            }
        });
    }

    private void setupScanPopup() {
        cancelScanPopupBtn.setOnAction(e -> {
            stopScanTimer();
            activeScanIndex = -1;
            activeScanCompleted = false;
            setNodeVisibility(fingerprintScanPopupOverlay, false);
        });

        confirmScanPopupBtn.setOnAction(e -> {
            stopScanTimer();
            if (activeScanIndex >= 0 && activeScanIndex < 4) {
                currentFingerprints[activeScanIndex] = true;
                applyFingerCardState(activeScanIndex, true);
                updateFingerprintCountLabel();
            }
            activeScanIndex = -1;
            activeScanCompleted = false;
            setNodeVisibility(fingerprintScanPopupOverlay, false);
        });
    }

    private void openScanPopupForFinger(int index) {
        if (!AppState.isScannerConnected()) {
            participantModalError.setText("Cannot scan: ZKTeco 9500 scanner is disconnected.");
            return;
        }

        stopScanTimer();
        participantModalError.setText("");
        activeScanIndex = index;
        activeScanCompleted = false;

        scanPopupFingerTitle.setText(FINGER_TITLES[index]);
        scanPopupStatusLabel.setText("Scanning fingerprint...");
        scanPopupImageView.setImage(blackFingerprintImage);
        setNodeVisibility(fingerprintScanPopupOverlay, true);

        activeScanTimer = new PauseTransition(Duration.seconds(1.2));
        activeScanTimer.setOnFinished(evt -> {
            activeScanCompleted = true;
            scanPopupImageView.setImage(greenFingerprintImage);
            scanPopupStatusLabel.setText("Fingerprint scanned! Click Done.");
        });
        activeScanTimer.play();
    }

    private void stopScanTimer() {
        if (activeScanTimer != null) {
            activeScanTimer.stop();
        }
    }

    private void applyFingerCardState(int index, boolean captured) {
        VBox card = getFingerCard(index);
        ImageView img = getFingerImageView(index);
        Label status = getFingerStatusLabel(index);

        if (captured) {
            if (!card.getStyleClass().contains("finger-card-captured")) {
                card.getStyleClass().add("finger-card-captured");
            }
            img.setImage(greenFingerprintImage);
            status.setText("Captured ✓");
        } else {
            card.getStyleClass().remove("finger-card-captured");
            img.setImage(blackFingerprintImage);
            status.setText("Tap to scan");
        }
    }

    private void updateFingerprintCountLabel() {
        int count = 0;
        for (boolean b : currentFingerprints) if (b) count++;
        scannerCountLabel.setText(count + " of 4 fingerprints captured");
    }

    private VBox getFingerCard(int index) {
        return switch (index) {
            case 0 -> leftThumbCard;
            case 1 -> leftIndexCard;
            case 2 -> rightThumbCard;
            default -> rightIndexCard;
        };
    }

    private ImageView getFingerImageView(int index) {
        return switch (index) {
            case 0 -> leftThumbImg;
            case 1 -> leftIndexImg;
            case 2 -> rightThumbImg;
            default -> rightIndexImg;
        };
    }

    private Label getFingerStatusLabel(int index) {
        return switch (index) {
            case 0 -> leftThumbStatus;
            case 1 -> leftIndexStatus;
            case 2 -> rightThumbStatus;
            default -> rightIndexStatus;
        };
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

        if (targetPage == reportsPage) {
            refreshReportsUI();
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

            AppState.Program newProgram = new AppState.Program(name, venue, start, end, desc);
            DatabaseManager.insertProgram(newProgram);
            AppState.getPrograms().setAll(DatabaseManager.loadAllPrograms());

            setNodeVisibility(programModalOverlay, false);
            refreshDashboardUI();
        });

        openParticipantModalBtn.setOnAction(e -> openParticipantModal());
        closeParticipantModalBtn.setOnAction(e -> {
            stopScanTimer();
            setNodeVisibility(fingerprintScanPopupOverlay, false);
            setNodeVisibility(participantModalOverlay, false);
        });
        cancelParticipantModalBtn.setOnAction(e -> {
            stopScanTimer();
            setNodeVisibility(fingerprintScanPopupOverlay, false);
            setNodeVisibility(participantModalOverlay, false);
        });

        leftThumbCard.setOnMouseClicked(e -> openScanPopupForFinger(0));
        leftIndexCard.setOnMouseClicked(e -> openScanPopupForFinger(1));
        rightThumbCard.setOnMouseClicked(e -> openScanPopupForFinger(2));
        rightIndexCard.setOnMouseClicked(e -> openScanPopupForFinger(3));

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
                AppState.Participant newParticipant = new AppState.Participant(
                        fullName, selectedProgram.getName(), phone, currentFingerprints.clone()
                );
                DatabaseManager.insertParticipant(newParticipant);
                AppState.getParticipants().setAll(DatabaseManager.loadAllParticipants());

                setNodeVisibility(participantModalOverlay, false);
                refreshDashboardUI();
            }
        });
    }

    private void setupReportsPage() {
        if (attendanceReportTable == null) return;

        colParticipantName.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFullName()));
        colParticipantProgram.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getProgramName()));
        colParticipantPhone.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPhone()));
        colFingerprintsEnrolled.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCapturedFingerprintCount() + " / 4"));
        colBiometricStatus.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCapturedFingerprintCount() == 4 ? "Verified" : "Incomplete"));

        reportProgramFilterCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!isRefreshingReports) {
                refreshReportsUI();
            }
        });

        exportReportBtn.setOnAction(e -> {
            try {
                Path outFile = Path.of("attendance-report.csv");
                StringBuilder csv = new StringBuilder("Participant Name,Program,Phone,Fingerprints Enrolled\n");
                for (AppState.Participant p : getFilteredParticipants()) {
                    csv.append(escapeCsv(p.getFullName())).append(",")
                            .append(escapeCsv(p.getProgramName())).append(",")
                            .append(escapeCsv(p.getPhone())).append(",")
                            .append(p.getCapturedFingerprintCount()).append("/4\n");
                }
                Files.writeString(outFile, csv.toString());
                reportStatusMessage.setText("Exported report to " + outFile.toAbsolutePath());
            } catch (Exception ex) {
                reportStatusMessage.setText("Failed to export CSV: " + ex.getMessage());
            }
        });
    }

    private List<AppState.Participant> getFilteredParticipants() {
        String selected = reportProgramFilterCombo.getValue();
        if (selected == null || selected.equals("All programs")) {
            return AppState.getParticipants();
        }
        return AppState.getParticipants().stream()
                .filter(p -> selected.equals(p.getProgramName()))
                .toList();
    }

    private void refreshReportsUI() {
        if (reportProgramFilterCombo == null || attendanceReportTable == null || isRefreshingReports) {
            return;
        }

        isRefreshingReports = true;
        try {
            String previousSelection = reportProgramFilterCombo.getValue();
            ObservableList<String> filterOptions = FXCollections.observableArrayList("All programs");
            for (AppState.Program prog : AppState.getPrograms()) {
                if (!filterOptions.contains(prog.getName())) {
                    filterOptions.add(prog.getName());
                }
            }

            if (!filterOptions.equals(reportProgramFilterCombo.getItems())) {
                reportProgramFilterCombo.setItems(filterOptions);
            }

            String nextSelection = (previousSelection != null && filterOptions.contains(previousSelection))
                    ? previousSelection
                    : "All programs";

            if (!nextSelection.equals(reportProgramFilterCombo.getValue())) {
                reportProgramFilterCombo.setValue(nextSelection);
            }

            List<AppState.Participant> filtered = getFilteredParticipants();
            attendanceReportTable.setItems(FXCollections.observableArrayList(filtered));

            long completeCount = filtered.stream()
                    .filter(p -> p.getCapturedFingerprintCount() == 4)
                    .count();

            reportTotalParticipantsLabel.setText(String.valueOf(filtered.size()));
            reportBiometricCompleteLabel.setText(String.valueOf(completeCount));
            reportActiveProgramsLabel.setText(String.valueOf(AppState.getPrograms().size()));
        } finally {
            isRefreshingReports = false;
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private void setupSettingsPage() {
        if (settingsScannerToggle == null) return;

        if (settingsStoragePathLabel != null) {
            settingsStoragePathLabel.setText(Path.of("attendance.db").toAbsolutePath().toString());
        }

        settingsScannerToggle.setSelected(AppState.isScannerConnected());
        settingsScannerToggle.setText(AppState.isScannerConnected() ? "Connected" : "Disconnected");

        settingsScannerToggle.setOnAction(e -> {
            boolean connected = settingsScannerToggle.isSelected();
            AppState.setScannerConnected(connected);
            settingsScannerToggle.setText(connected ? "Connected" : "Disconnected");
            settingsFeedbackLabel.setText("Scanner status updated to " + (connected ? "Connected" : "Disconnected") + ".");
        });

        AppState.scannerConnectedProperty().addListener((obs, oldVal, connected) -> {
            settingsScannerToggle.setSelected(connected);
            settingsScannerToggle.setText(connected ? "Connected" : "Disconnected");
        });

        settingsToggleSidebarBtn.setOnAction(e -> {
            setSidebarCollapsed(!isSidebarCollapsed);
            settingsFeedbackLabel.setText("Sidebar " + (isSidebarCollapsed ? "collapsed." : "expanded."));
        });

        settingsRefreshDataBtn.setOnAction(e -> {
            AppState.getPrograms().setAll(DatabaseManager.loadAllPrograms());
            AppState.getParticipants().setAll(DatabaseManager.loadAllParticipants());
            refreshDashboardUI();
            settingsFeedbackLabel.setText("Reloaded records from SQLite database.");
        });

        settingsClearDataBtn.setOnAction(e -> {
            DatabaseManager.clearAllRecords();
            AppState.getParticipants().clear();
            AppState.getPrograms().clear();
            refreshDashboardUI();
            settingsFeedbackLabel.setText("All program and participant records cleared from SQLite.");
        });
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
        activeScanIndex = -1;
        activeScanCompleted = false;
        setNodeVisibility(fingerprintScanPopupOverlay, false);

        for (int i = 0; i < 4; i++) {
            currentFingerprints[i] = false;
            applyFingerCardState(i, false);
        }
        updateFingerprintCountLabel();

        participantProgramCombo.setItems(AppState.getPrograms());
        participantProgramCombo.setValue(null);

        setNodeVisibility(participantModalOverlay, true);
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

        refreshReportsUI();
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
            DatabaseManager.deleteProgram(program.getName());
            AppState.getPrograms().setAll(DatabaseManager.loadAllPrograms());
            AppState.getParticipants().setAll(DatabaseManager.loadAllParticipants());
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