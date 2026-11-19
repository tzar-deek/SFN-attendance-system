package com.skillsfusionnetwork.student_attendance_sys;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AppState {
    private static String currentUsername = "Admin";
    private static final ObservableList<Program> programs = FXCollections.observableArrayList();
    private static final ObservableList<Participant> participants = FXCollections.observableArrayList();

    // Global Scanner Connection State (Default: Connected = true)
    private static final BooleanProperty scannerConnected = new SimpleBooleanProperty(true);

    public static String getCurrentUsername() { return currentUsername; }
    public static void setCurrentUsername(String username) {
        if (username != null && !username.trim().isEmpty()) {
            currentUsername = username.trim();
        }
    }

    public static ObservableList<Program> getPrograms() { return programs; }
    public static ObservableList<Participant> getParticipants() { return participants; }

    public static BooleanProperty scannerConnectedProperty() { return scannerConnected; }
    public static boolean isScannerConnected() { return scannerConnected.get(); }
    public static void setScannerConnected(boolean connected) { scannerConnected.set(connected); }

    public static void removeProgram(Program program) {
        if (program != null) {
            programs.remove(program);
            participants.removeIf(p -> p.getProgramName().equals(program.getName()));
        }
    }

    public static void removeParticipant(Participant participant) {
        if (participant != null) participants.remove(participant);
    }

    public static void clearSession() {
        programs.clear();
        participants.clear();
    }

    public static class Program {
        private final String name;
        private final String venue;
        private final String startDate;
        private final String endDate;
        private final String description;

        public Program(String name, String venue, String startDate, String endDate, String description) {
            this.name = name;
            this.venue = venue;
            this.startDate = startDate;
            this.endDate = endDate;
            this.description = description;
        }

        public String getName() { return name; }
        public String getVenue() { return venue; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
        public String getDescription() { return description; }

        public long getParticipantCount() {
            return participants.stream().filter(p -> p.getProgramName().equals(this.name)).count();
        }

        @Override
        public String toString() { return name; }
    }

    public static class Participant {
        private final String fullName;
        private final String programName;
        private final String phone;
        private final boolean[] fingerprints;

        public Participant(String fullName, String programName, String phone, boolean[] fingerprints) {
            this.fullName = fullName;
            this.programName = programName;
            this.phone = phone;
            this.fingerprints = fingerprints;
        }

        public String getFullName() { return fullName; }
        public String getProgramName() { return programName; }
        public String getPhone() { return phone; }
        public boolean[] getFingerprints() { return fingerprints; }
    }
}