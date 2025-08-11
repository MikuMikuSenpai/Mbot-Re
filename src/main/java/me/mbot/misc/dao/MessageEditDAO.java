package me.mbot.misc.dao;

import me.mbot.configuration.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class MessageEditDAO {
    private static final String DB_URL = Constants.getDBUrl();
    private static final String DB_USER = Constants.getDBUser();
    private static final String DB_PASSWORD = Constants.getDBPassword();
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageEditDAO.class);

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void insertMessageEdit(String messageId, long userId, String oldContent, String newContent) {
        String sql = "REPLACE INTO message_edit_logs (message_id, user_id, old_content, new_content) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, messageId);
            stmt.setLong(2, userId);
            stmt.setString(3, oldContent);
            stmt.setString(4, newContent);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void deleteMessageEdit(String messageId) {
        String sql = "DELETE FROM message_edit_logs WHERE message_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, messageId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static String getOldMessageContent(String messageId) {
        String sql = "SELECT old_content FROM message_edit_logs WHERE message_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, messageId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("old_content");
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }
}