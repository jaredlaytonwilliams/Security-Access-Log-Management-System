import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AuthManager {

    private static final String DB_URL = "jdbc:sqlite:database/security_logs.db";

    // Method to authenticate a user
    public static String authenticateUser(String username, String password) {
        String hashedPassword = hashPassword(password);
        return querySingleResult("SELECT role FROM Users WHERE username = ? AND password = ?", rs -> rs.getString("role"), username, hashedPassword);
    }

    // Method to register a new user
    public static int registerUser(String username, String password, String role) {
        String hashedPassword = hashPassword(password);
        if (usernameExists(username)) {
            System.out.println("Username already exists. Please choose a different username.");
            return -1;
        }
        String sql = "INSERT INTO Users(username, password, role) VALUES(?, ?, ?)";
        return executeUpdateAndGetId(sql, username, hashedPassword, role);
    }

    public static int getUserId(String username) {
        return querySingleResult("SELECT id FROM Users WHERE username = ?", rs -> rs.getInt("id"), username);
    }
    
    // Simple method to hash passwords using SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(Integer.toHexString(0xff & b));
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // Helper methods
    private static boolean usernameExists(String username) {
        return querySingleResult("SELECT COUNT(*) FROM Users WHERE username = ?", rs -> rs.getInt(1) > 0, username);
    }

    private static <T> T querySingleResult(String sql, ResultSetHandler<T> handler, String... params) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setString(i + 1, params[i]);
            }
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return handler.handle(rs);
            }
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private static int executeUpdateAndGetId(String sql, String... params) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setString(i + 1, params[i]);
            }
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    @FunctionalInterface
    private interface ResultSetHandler<T> {
        T handle(ResultSet rs) throws SQLException;
    }
}
