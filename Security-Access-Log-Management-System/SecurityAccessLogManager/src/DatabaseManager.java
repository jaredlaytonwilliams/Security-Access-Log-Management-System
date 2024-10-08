import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:database/security_logs.db";

    public static void connect() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                System.out.println("Connected to the database");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createTables() {
        String usersTable = "CREATE TABLE IF NOT EXISTS Users (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "username TEXT NOT NULL UNIQUE," +
                            "password TEXT NOT NULL," +
                            "role TEXT NOT NULL" +
                            ");";
    
        String accessLogsTable = "CREATE TABLE IF NOT EXISTS AccessLogs (" +
                                 "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                 "user_id INTEGER," +
                                 "access_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
                                 "status TEXT," +
                                 "action TEXT," +
                                 "ip_address TEXT," +
                                 "FOREIGN KEY (user_id) REFERENCES Users(id)" +
                                 ");";
    
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
    
            stmt.execute(usersTable);
            stmt.execute(accessLogsTable);
    
            System.out.println("Tables created successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void logAccessEvent(int userId, String status, String action, String ipAddress) {
        String sql = "INSERT INTO AccessLogs(user_id, status, action, ip_address) VALUES(?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, status);
            pstmt.setString(3, action);
            pstmt.setString(4, ipAddress);
            pstmt.executeUpdate();
            System.out.println("Access event logged successfully");
        } catch (Exception e) {
            System.out.println("Error logging access event: " + e.getMessage());
        }
    }

    public static void displayAccessLogs() {
        String sql = "SELECT * FROM AccessLogs";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                                   ", User ID: " + rs.getInt("user_id") +
                                   ", Access Time: " + rs.getString("access_time") +
                                   ", Status: " + rs.getString("status") +
                                   ", Action: " + rs.getString("action") +
                                   ", IP Address: " + rs.getString("ip_address"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
