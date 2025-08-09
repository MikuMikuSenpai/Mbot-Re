package me.mbot.misc.dao;

import me.mbot.configuration.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AntiSpamFilterDAO {
    private static final String DB_URL = Constants.getDBUrl();
    private static final String DB_USER = Constants.getDBUser();
    private static final String DB_PASSWORD = Constants.getDBPassword();
    private static final Logger logger = LoggerFactory.getLogger(AntiSpamFilterDAO.class);

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void insertMessageTimestamp(String userId, long timestamp) {
        String sql = "INSERT INTO spam_tracker (user_id, timestamp_ms) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setLong(2, timestamp);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static List<Long> getRecentTimestamps(String userId, long cutoff) {
        List<Long> timestamps = new ArrayList<>();
        String sql = "SELECT timestamp_ms FROM spam_tracker WHERE user_id = ? AND timestamp_ms >= ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setLong(2, cutoff);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                timestamps.add(rs.getLong("timestamp_ms"));
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
        return timestamps;
    }

    public static void deleteUserTimestamps(String userId) {
        String sql = "DELETE FROM spam_tracker WHERE user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
