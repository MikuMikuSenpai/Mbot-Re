package me.mbot.misc.dao;

import me.mbot.configuration.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

public class MessageDeleteDAO {
    private static final String DB_URL = Constants.getDBUrl();
    private static final String DB_USER = Constants.getDBUser();
    private static final String DB_PASSWORD = Constants.getDBPassword();
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDeleteDAO.class);

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void insertDeletedMessage(String messageId, long userId, String channelId, String content, List<String> imageUrls, List<String> videoUrls) {
        String sql = "REPLACE INTO message_delete_logs (message_id, user_id, channel_id, content, image_urls, video_urls) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, messageId);
            stmt.setLong(2, userId);
            stmt.setString(3, channelId);
            stmt.setString(4, content);
            stmt.setString(5, String.join(",", imageUrls));
            stmt.setString(6, String.join(",", videoUrls));
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static List<String> getImageUrls(String messageId) {
        String sql = "SELECT image_urls FROM message_delete_logs WHERE message_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, messageId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String csv = rs.getString("image_urls");
                return csv == null || csv.isEmpty() ? List.of() : List.of(csv.split(","));
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return List.of();
    }

    public static List<String> getVideoUrls(String messageId) {
        String sql = "SELECT video_urls FROM message_delete_logs WHERE message_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, messageId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String csv = rs.getString("video_urls");
                return csv == null || csv.isEmpty() ? List.of() : List.of(csv.split(","));
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return List.of();
    }

    public static String getDeletedContent(String messageId) {
        String sql = "SELECT content FROM message_delete_logs WHERE message_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, messageId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("content");
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    public static String getAuthorId(String messageId) {
        String sql = "SELECT user_id FROM message_delete_logs WHERE message_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, messageId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("user_id");
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    public static String getChannelId(String messageId) {
        String sql = "SELECT channel_id FROM message_delete_logs WHERE message_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, messageId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("channel_id");
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }
}
