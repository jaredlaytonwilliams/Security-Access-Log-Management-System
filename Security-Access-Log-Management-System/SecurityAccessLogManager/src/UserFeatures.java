import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserFeatures {

    private static final String DB_URL = "jdbc:sqlite:database/security_logs.db";

    // Method to view user's own logs
    public static void viewOwnLogs(String username) {
        String sql = "SELECT * FROM AccessLogs WHERE user_id = (SELECT id FROM Users WHERE username = ?)";
        executeQuery(sql, rs -> displayLog(rs), username);
    }

    // Method to view login statistics
    public static void viewLoginStatistics(String username) {
        String sql = "SELECT COUNT(*) AS login_count, DATE(access_time) AS login_date " +
                     "FROM AccessLogs WHERE action = 'Login' AND status = 'Success' " +
                     "AND user_id = (SELECT id FROM Users WHERE username = ?) " +
                     "GROUP BY login_date ORDER BY login_date DESC";
        executeQuery(sql, rs -> {
            System.out.println("Date: " + rs.getString("login_date") + 
                               " - Successful Logins: " + rs.getInt("login_count"));
        }, username);
    }

    // Utility method to display a single log entry
    private static void displayLog(ResultSet rs) throws SQLException {
        System.out.println("Log ID: " + rs.getInt("id"));
        System.out.println("User ID: " + rs.getInt("user_id"));
        System.out.println("Access Time: " + rs.getString("access_time"));
        System.out.println("Status: " + rs.getString("status"));
        System.out.println("Action: " + rs.getString("action"));
        System.out.println("IP Address: " + rs.getString("ip_address"));
        System.out.println("----------------------------");
    }

    // Helper method for executing queries
    private static void executeQuery(String sql, ResultSetHandler handler, String... params) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setString(i + 1, params[i]);
            }
            ResultSet rs = pstmt.executeQuery();
            if (!rs.isBeforeFirst()) {
                System.out.println("No logs found.");
            } else {
                while (rs.next()) {
                    handler.handle(rs);
                }
            }
        } catch (Exception e) {
            System.out.println("Error retrieving data: " + e.getMessage());
        }
    }

    @FunctionalInterface
    private interface ResultSetHandler {
        void handle(ResultSet rs) throws SQLException;
    }
}
