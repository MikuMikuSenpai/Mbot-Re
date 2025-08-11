package me.mbot.misc.dao;

import me.mbot.configuration.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TodoDAO {
    private static final String DB_URL = Constants.getDBUrl();
    private static final String DB_USER = Constants.getDBUser();
    private static final String DB_PASSWORD = Constants.getDBPassword();
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoDAO.class);

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public static void addTodo(long userId, String todo) {
        String sql = "INSERT INTO user_todos (user_id, todo) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setString(2, todo);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static List<TodoEntry> getTodos(long userId) {
        List<TodoEntry> todos = new ArrayList<>();
        String sql = "SELECT id, todo FROM user_todos WHERE user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                todos.add(new TodoEntry(
                        rs.getInt("id"),
                        rs.getString("todo")
                ));
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        return todos;
    }

    public static int getTodoCount(long userId) {
        String sql = "SELECT COUNT(*) FROM user_todos WHERE user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
        return 0;
    }

    public static void removeTodoById(int id) {
        String sql = "DELETE FROM user_todos WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
