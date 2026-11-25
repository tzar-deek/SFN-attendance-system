package com.skillsfusionnetwork.student_attendance_sys;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:attendance.db";

    private DatabaseManager() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        String createProgramsTable = """
                CREATE TABLE IF NOT EXISTS programs (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE,
                    venue TEXT NOT NULL,
                    start_date TEXT NOT NULL,
                    end_date TEXT NOT NULL,
                    description TEXT
                );
                """;

        String createParticipantsTable = """
                CREATE TABLE IF NOT EXISTS participants (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    full_name TEXT NOT NULL,
                    program_name TEXT NOT NULL,
                    phone TEXT,
                    finger_left_thumb INTEGER NOT NULL DEFAULT 0,
                    finger_left_index INTEGER NOT NULL DEFAULT 0,
                    finger_right_thumb INTEGER NOT NULL DEFAULT 0,
                    finger_right_index INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY (program_name) REFERENCES programs(name) ON DELETE CASCADE
                );
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            stmt.execute(createProgramsTable);
            stmt.execute(createParticipantsTable);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize SQLite database: " + e.getMessage(), e);
        }
    }

    public static void insertProgram(AppState.Program program) {
        if (program == null) return;

        String sql = """
                INSERT OR REPLACE INTO programs (name, venue, start_date, end_date, description)
                VALUES (?, ?, ?, ?, ?);
                """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, program.getName());
            pstmt.setString(2, program.getVenue());
            pstmt.setString(3, program.getStartDate());
            pstmt.setString(4, program.getEndDate());
            pstmt.setString(5, program.getDescription());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert program: " + e.getMessage(), e);
        }
    }

    public static void deleteProgram(String programName) {
        if (programName == null || programName.isBlank()) return;

        String deleteParticipantsSql = "DELETE FROM participants WHERE program_name = ?;";
        String deleteProgramSql = "DELETE FROM programs WHERE name = ?;";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pStmt = conn.prepareStatement(deleteParticipantsSql);
                 PreparedStatement progStmt = conn.prepareStatement(deleteProgramSql)) {
                pStmt.setString(1, programName);
                pStmt.executeUpdate();

                progStmt.setString(1, programName);
                progStmt.executeUpdate();

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete program: " + e.getMessage(), e);
        }
    }

    public static List<AppState.Program> loadAllPrograms() {
        List<AppState.Program> results = new ArrayList<>();
        String sql = "SELECT name, venue, start_date, end_date, description FROM programs ORDER BY id ASC;";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(new AppState.Program(
                        rs.getString("name"),
                        rs.getString("venue"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load programs: " + e.getMessage(), e);
        }
        return results;
    }

    public static void insertParticipant(AppState.Participant participant) {
        if (participant == null) return;

        boolean[] fps = participant.getFingerprints();
        boolean f0 = fps != null && fps.length > 0 && fps[0];
        boolean f1 = fps != null && fps.length > 1 && fps[1];
        boolean f2 = fps != null && fps.length > 2 && fps[2];
        boolean f3 = fps != null && fps.length > 3 && fps[3];

        String sql = """
                INSERT INTO participants (
                    full_name, program_name, phone,
                    finger_left_thumb, finger_left_index, finger_right_thumb, finger_right_index
                ) VALUES (?, ?, ?, ?, ?, ?, ?);
                """;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, participant.getFullName());
            pstmt.setString(2, participant.getProgramName());
            pstmt.setString(3, participant.getPhone());
            pstmt.setInt(4, f0 ? 1 : 0);
            pstmt.setInt(5, f1 ? 1 : 0);
            pstmt.setInt(6, f2 ? 1 : 0);
            pstmt.setInt(7, f3 ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert participant: " + e.getMessage(), e);
        }
    }

    public static List<AppState.Participant> loadAllParticipants() {
        List<AppState.Participant> results = new ArrayList<>();
        String sql = """
                SELECT full_name, program_name, phone,
                       finger_left_thumb, finger_left_index, finger_right_thumb, finger_right_index
                FROM participants
                ORDER BY id ASC;
                """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                boolean[] fps = new boolean[]{
                        rs.getInt("finger_left_thumb") == 1,
                        rs.getInt("finger_left_index") == 1,
                        rs.getInt("finger_right_thumb") == 1,
                        rs.getInt("finger_right_index") == 1
                };
                results.add(new AppState.Participant(
                        rs.getString("full_name"),
                        rs.getString("program_name"),
                        rs.getString("phone"),
                        fps
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load participants: " + e.getMessage(), e);
        }
        return results;
    }

    public static void clearAllRecords() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM participants;");
            stmt.execute("DELETE FROM programs;");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear database records: " + e.getMessage(), e);
        }
    }
}