package com.timetable.db;

import com.timetable.model.Staff;
import com.timetable.model.Subject;
import com.timetable.model.TimetableEntry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String URL  = "jdbc:mysql://localhost:3306/timetable_db";
    private static final String USER = "timetable_user";
    private static final String PASS = "timetable123";

    private Connection conn;

    public void connect() throws SQLException {
        conn = DriverManager.getConnection(URL, USER, PASS);
        System.out.println("Connected to MySQL.");
    }

    // ── SUBJECTS ──────────────────────────────────────────────────

    public void saveSubject(Subject s) throws SQLException {
        String sql = "INSERT INTO subjects (code, name, credits, has_lab) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE name=VALUES(name), " +
                     "credits=VALUES(credits), has_lab=VALUES(has_lab)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getCode());
            ps.setString(2, s.getName());
            ps.setInt(3, s.getCredits());
            ps.setInt(4, s.hasLab() ? 1 : 0);
            ps.executeUpdate();
        }
    }

    public List<Subject> loadAllSubjects() throws SQLException {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT * FROM subjects";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Subject(
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getInt("credits"),
                    rs.getInt("has_lab") == 1
                ));
            }
        }
        return list;
    }

    // ── STAFF ─────────────────────────────────────────────────────

    public void saveStaff(Staff s) throws SQLException {
        String sql = "INSERT INTO staff (id, name, subject_code) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE name=VALUES(name)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getId());
            ps.setString(2, s.getName());
            ps.setString(3, s.getSubjectCode());
            ps.executeUpdate();
        }
    }

    public List<Staff> loadAllStaff() throws SQLException {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM staff";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Staff(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getString("subject_code")
                ));
            }
        }
        return list;
    }

    // ── TIMETABLE ─────────────────────────────────────────────────

    public void saveTimetableEntry(String section, int day, int period,
                                    TimetableEntry entry) throws SQLException {
        String sql = "INSERT INTO timetable_entries " +
                     "(section, day_index, period_index, subject_code, " +
                     "staff_id, is_lab, is_lab_part2) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, section);
            ps.setInt(2, day);
            ps.setInt(3, period);
            ps.setString(4, entry.getSubject().getCode());
            ps.setString(5, entry.getStaff() != null ? entry.getStaff().getId() : null);
            ps.setInt(6, entry.isLab() ? 1 : 0);
            ps.setInt(7, entry.isLabPart2() ? 1 : 0);
            ps.executeUpdate();
        }
    }

    public void clearTimetableForSection(String section) throws SQLException {
        String sql = "DELETE FROM timetable_entries WHERE section = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, section);
            ps.executeUpdate();
        }
    }

    // ── CLOSE ─────────────────────────────────────────────────────

    public void close() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}