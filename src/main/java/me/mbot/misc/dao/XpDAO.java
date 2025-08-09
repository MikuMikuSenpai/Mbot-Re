package me.mbot.misc.dao;

import me.mbot.configuration.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class XpDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(XpDAO.class);

    private static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    Constants.getDBUrl(),
                    Constants.getDBUser(),
                    Constants.getDBPassword());
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static int getXP(long userId) {
        try (Connection conn = getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT xp FROM user_xp WHERE user_id = ?");
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("xp");
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        return 0;
    }

    public static void addXP(long userId, int xpToAdd) {
        try (Connection conn = getConnection()) {
            // on duplicate = if there is another user with
            // same primary key (user id) it doesnt insert a new row but updates instead.
            PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO user_xp (user_id, xp) VALUES (?, ?) ON DUPLICATE KEY UPDATE xp = xp + ?");
            insert.setLong(1, userId);
            insert.setInt(2, xpToAdd);
            insert.setInt(3, xpToAdd);
            insert.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
}