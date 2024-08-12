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
        String sql = "SELECT role FROM Users WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("role");  // Return the role of the user
            } else {
                return null;  // Authentication failed
            }

        } catch (Exception e) {
            System.out.println("Authentication error: " + e.getMessage());
            return null;
        }
    }

    // Method to register a new user
    public static int registerUser(String username, String password, String role) {
        String hashedPassword = hashPassword(password);
        String checkUserSql = "SELECT COUNT(*) FROM Users WHERE username = ?";
        String sql = "INSERT INTO Users(username, password, role) VALUES(?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement checkStmt = conn.prepareStatement(checkUserSql);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Check if username already exists
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Username already exists. Please choose a different username.");
                return -1;
            }

            // Proceed with registration
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, role);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            // Retrieve the generated user ID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    System.out.println("User registered successfully. User ID: " + userId);
                    return userId;
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

        } catch (Exception e) {
            System.out.println("Registration error: " + e.getMessage());
            return -1;
        }
    }

    public static int getUserId(String username) {
        String sql = "SELECT id FROM Users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
    
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                return -1; // User not found
            }
        } catch (Exception e) {
            System.out.println("Error getting user ID: " + e.getMessage());
            return -1;
        }
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
}
