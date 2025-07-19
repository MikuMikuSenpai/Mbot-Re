package me.mbot.misc.dao;

import me.mbot.configuration.Constants;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReminderDAO {
    private static final String DB_URL = Constants.getDBUrl();
    private static final String DB_USER = Constants.getDBUser();
    private static final String DB_PASSWORD = Constants.getDBPassword();

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void insertReminder(long userId, Timestamp remindAt, String note) {
        String sql = "INSERT INTO reminders (user_id, remind_at, note) VALUES (?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setTimestamp(2, remindAt);
            stmt.setString(3, note);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Reminder> getDueReminders() {
        List<Reminder> reminders = new ArrayList<>();
        String sql = "SELECT id, user_id, remind_at, note FROM reminders WHERE remind_at <= NOW()";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reminders.add(new Reminder(
                        rs.getInt("id"),
                        rs.getLong("user_id"),
                        rs.getTimestamp("remind_at"),
                        rs.getString("note")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reminders;
    }

    public static void deleteReminder(int id) {
        String sql = "DELETE FROM reminders WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public record Reminder(int id, long userId, Timestamp remindAt, String note) {}
}