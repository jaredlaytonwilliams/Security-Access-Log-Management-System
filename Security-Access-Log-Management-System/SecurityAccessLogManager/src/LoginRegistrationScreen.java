import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.*;

public class LoginRegistrationScreen {

    private static String ipAddress;

    public static void show() {
        ipAddress = getLocalIpAddress();

        JFrame frame = new JFrame("User Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JButton registerButton = new JButton("Register");
        JButton loginButton = new JButton("Login");

        frame.setLayout(new GridLayout(2, 1));
        frame.add(registerButton);
        frame.add(loginButton);

        registerButton.addActionListener(e -> handleUserRegistration(ipAddress, frame));
        loginButton.addActionListener(e -> handleUserLogin(ipAddress, frame));

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    public static String getIpAddress() {
        return ipAddress;
    }
    
    private static String getLocalIpAddress() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("Error getting IP address: " + e.getMessage());
            return "Unknown";
        }
    }

    private static void handleUserRegistration(String ipAddress, JFrame frame) {
        JTextField usernameField = new JTextField();
        JTextField passwordField = new JPasswordField();
        String[] roles = {"User", "Admin"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);

        Object[] message = {
            "Username:", usernameField,
            "Password:", passwordField,
            "Role:", roleComboBox
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Register", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String role = (String) roleComboBox.getSelectedItem();

            int userId = AuthManager.registerUser(username, password, role);
            if (userId != -1) {
                DatabaseManager.logAccessEvent(userId, "Success", "Registration", ipAddress);
                JOptionPane.showMessageDialog(null, "Registration successful.");
                handleUserLoginFlow(username, role, userId, frame);
            } else {
                // Log failed registration attempt
                DatabaseManager.logAccessEvent(0, "Failed", "Registration", ipAddress);
                JOptionPane.showMessageDialog(null, "Registration failed. Username might already exist.");
            }
        }
    }

    private static void handleUserLogin(String ipAddress, JFrame frame) {
        JTextField usernameField = new JTextField();
        JTextField passwordField = new JPasswordField();

        Object[] message = {
            "Username:", usernameField,
            "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String role = AuthManager.authenticateUser(username, password);

            int userId = AuthManager.getUserId(username);

            if (role != null && userId != -1) {
                DatabaseManager.logAccessEvent(userId, "Success", "Login", ipAddress);
                handleUserLoginFlow(username, role, userId, frame);
            } else {
                // Log failed login attempt
                if (userId != -1) {
                    DatabaseManager.logAccessEvent(userId, "Failed", "Login", ipAddress);
                } else {
                    DatabaseManager.logAccessEvent(0, "Failed", "Login", ipAddress);
                }
                JOptionPane.showMessageDialog(null, "Access denied. Invalid username or password.");
            }
        }
    }

    private static void handleUserLoginFlow(String username, String role, int userId, JFrame frame) {
        frame.dispose();  // Close the login/register frame

        if (role.equalsIgnoreCase("Admin")) {
            MainMenu.showAdminMenu(username, userId);
        } else if (role.equalsIgnoreCase("User")) {
            MainMenu.showUserMenu(username);
        } else {
            JOptionPane.showMessageDialog(null, "Unknown role. Access restricted.");
        }
    }
}
