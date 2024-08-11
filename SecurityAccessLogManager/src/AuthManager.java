import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class AuthManager {

    private static final String DB_URL = "jdbc:sqlite:database/security_logs.db";

    // Method to register a new user
    public static void registerUser(String username, String password, String role) {
        String hashedPassword = hashPassword(password);

        String sql = "INSERT INTO Users(username, password, role) VALUES(?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.setString(3, role);
            pstmt.executeUpdate();
            System.out.println("User registered successfully");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // Simple method to hash passwords using SHA-256
    private static String hashPassword(String password) {
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