import java.awt.GridLayout;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;

public class MainMenu {

    public static void showAdminMenu(String username, int userId) {
        JFrame adminFrame = new JFrame("Admin Menu");
        adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        adminFrame.setSize(400, 300);
        adminFrame.setLayout(new GridLayout(4, 1));

        JButton userManagementButton = new JButton("User Management");
        JButton accessLogsButton = new JButton("Access Logs");
        JButton statisticsButton = new JButton("Statistics");
        JButton exitButton = new JButton("Exit");

        adminFrame.add(userManagementButton);
        adminFrame.add(accessLogsButton);
        adminFrame.add(statisticsButton);
        adminFrame.add(exitButton);

        userManagementButton.addActionListener(e -> showUserManagementMenu(adminFrame, userId));
        accessLogsButton.addActionListener(e -> showAccessLogsMenu(adminFrame, userId));
        statisticsButton.addActionListener(e -> showStatisticsMenu(adminFrame, userId));
        exitButton.addActionListener(e -> System.exit(0));

        adminFrame.setLocationRelativeTo(null);
        adminFrame.setVisible(true);
    }

    public static void showUserMenu(String username) {
        JFrame userFrame = new JFrame("User Menu");
        userFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        userFrame.setSize(400, 300);
        userFrame.setLayout(new GridLayout(3, 1));

        JButton viewLogsButton = new JButton("View My Logs");
        JButton viewStatsButton = new JButton("View My Login Statistics");
        JButton exitButton = new JButton("Exit");

        userFrame.add(viewLogsButton);
        userFrame.add(viewStatsButton);
        userFrame.add(exitButton);

        viewLogsButton.addActionListener(e -> UserFeatures.viewOwnLogs(username));
        viewStatsButton.addActionListener(e -> UserFeatures.viewLoginStatistics(username));
        exitButton.addActionListener(e -> System.exit(0));

        userFrame.setLocationRelativeTo(null);
        userFrame.setVisible(true);
    }

    private static void showUserManagementMenu(JFrame parentFrame, int userId) {
        parentFrame.dispose();  // Close the main menu

        JFrame userManagementFrame = new JFrame("User Management");
        userManagementFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        userManagementFrame.setSize(400, 300);
        userManagementFrame.setLayout(new GridLayout(4, 1));

        JButton addUserButton = new JButton("Add User");
        JButton deleteUserButton = new JButton("Delete User");
        JButton viewUsersButton = new JButton("View All Users");
        JButton backButton = new JButton("Back");

        addUserButton.addActionListener(e -> handleAddUser(userId));
        deleteUserButton.addActionListener(e -> handleDeleteUser(userId));
        viewUsersButton.addActionListener(e -> viewAllUsers());
        backButton.addActionListener(e -> {
            userManagementFrame.dispose();  // Close the submenu
            showAdminMenu(null, userId);  // Return to main menu
        });

        userManagementFrame.add(addUserButton);
        userManagementFrame.add(deleteUserButton);
        userManagementFrame.add(viewUsersButton);
        userManagementFrame.add(backButton);

        userManagementFrame.setLocationRelativeTo(null);
        userManagementFrame.setVisible(true);
    }

    private static void showAccessLogsMenu(JFrame parentFrame, int userId) {
        parentFrame.dispose();  // Close the main menu

        JFrame accessLogsFrame = new JFrame("Access Logs");
        accessLogsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        accessLogsFrame.setSize(400, 300);
        accessLogsFrame.setLayout(new GridLayout(6, 1));

        JButton viewLogsButton = new JButton("View All Access Logs");
        JButton reportByDateButton = new JButton("Generate Report by Date");
        JButton reportByUserButton = new JButton("Generate Report by User");
        JButton reportByActionTypeButton = new JButton("Generate Report by Action Type");
        JButton reportByOutcomeButton = new JButton("Generate Report by Outcome");
        JButton backButton = new JButton("Back");

        viewLogsButton.addActionListener(e -> AdminFeatures.viewAllLogs());
        reportByDateButton.addActionListener(e -> handleGenerateReportByDate(userId));
        reportByUserButton.addActionListener(e -> handleGenerateReportByUser(userId));
        reportByActionTypeButton.addActionListener(e -> handleGenerateReportByActionType(userId));
        reportByOutcomeButton.addActionListener(e -> handleGenerateReportByOutcome(userId));
        backButton.addActionListener(e -> {
            accessLogsFrame.dispose();  // Close the submenu
            showAdminMenu(null, userId);  // Return to main menu
        });

        accessLogsFrame.add(viewLogsButton);
        accessLogsFrame.add(reportByDateButton);
        accessLogsFrame.add(reportByUserButton);
        accessLogsFrame.add(reportByActionTypeButton);
        accessLogsFrame.add(reportByOutcomeButton);
        accessLogsFrame.add(backButton);

        accessLogsFrame.setLocationRelativeTo(null);
        accessLogsFrame.setVisible(true);
    }

    private static void showStatisticsMenu(JFrame parentFrame, int userId) {
        parentFrame.dispose();  // Close the main menu

        JFrame statisticsFrame = new JFrame("Statistics");
        statisticsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        statisticsFrame.setSize(400, 300);
        statisticsFrame.setLayout(new GridLayout(3, 1));

        JButton loginStatsButton = new JButton("Display Login Statistics");
        JButton mostActiveUsersButton = new JButton("Display Most Active Users");
        JButton backButton = new JButton("Back");

        loginStatsButton.addActionListener(e -> AdminFeatures.displayLoginStatistics());
        mostActiveUsersButton.addActionListener(e -> AdminFeatures.displayMostActiveUsers());
        backButton.addActionListener(e -> {
            statisticsFrame.dispose();  // Close the submenu
            showAdminMenu(null, userId);  // Return to main menu
        });

        statisticsFrame.add(loginStatsButton);
        statisticsFrame.add(mostActiveUsersButton);
        statisticsFrame.add(backButton);

        statisticsFrame.setLocationRelativeTo(null);
        statisticsFrame.setVisible(true);
    }

    private static void handleAddUser(int userId) {
        JTextField usernameField = new JTextField();
        JTextField passwordField = new JPasswordField();
        String[] roles = {"User", "Admin"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);

        Object[] message = {
            "Username:", usernameField,
            "Password:", passwordField,
            "Role:", roleComboBox
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add User", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String role = (String) roleComboBox.getSelectedItem();

            AuthManager.registerUser(username, password, role);
            DatabaseManager.logAccessEvent(userId, "Success", "Add User", LoginRegistrationScreen.getIpAddress());
            JOptionPane.showMessageDialog(null, "User added successfully.");
        }
    }

    private static void handleDeleteUser(int userId) {
        String usernameToDelete = JOptionPane.showInputDialog("Enter the username to delete:");
        if (usernameToDelete != null && !usernameToDelete.isEmpty()) {
            if (AdminFeatures.deleteUser(usernameToDelete)) {
                DatabaseManager.logAccessEvent(userId, "Success", "Delete User", LoginRegistrationScreen.getIpAddress());
                JOptionPane.showMessageDialog(null, "User deleted successfully.");
            } else {
                DatabaseManager.logAccessEvent(userId, "Failed", "Delete User", LoginRegistrationScreen.getIpAddress());
                JOptionPane.showMessageDialog(null, "User not found.");
            }
        }
    }

    private static void handleGenerateReportByDate(int userId) {
        JTextField startDateField = new JTextField();
        JTextField endDateField = new JTextField();

        Object[] message = {
            "Start Date (YYYY-MM-DD):", startDateField,
            "End Date (YYYY-MM-DD):", endDateField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Generate Report by Date", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String startDate = startDateField.getText();
            String endDate = endDateField.getText();
            AdminFeatures.filterLogsByDate(startDate, endDate);
            DatabaseManager.logAccessEvent(userId, "Success", "Generate Report by Date", LoginRegistrationScreen.getIpAddress());
            JOptionPane.showMessageDialog(null, "Report generated by date.");
        }
    }

    private static void handleGenerateReportByUser(int userId) {
        String usernameFilter = JOptionPane.showInputDialog("Enter username to filter logs:");
        if (usernameFilter != null && !usernameFilter.isEmpty()) {
            AdminFeatures.filterLogsByUser(usernameFilter);
            DatabaseManager.logAccessEvent(userId, "Success", "Generate Report by User", LoginRegistrationScreen.getIpAddress());
            JOptionPane.showMessageDialog(null, "Report generated by user.");
        }
    }

    private static void handleGenerateReportByActionType(int userId) {
        String actionType = JOptionPane.showInputDialog("Enter action type to filter logs (e.g., Login, Logout):");
        if (actionType != null && !actionType.isEmpty()) {
            AdminFeatures.filterLogsByActionType(actionType);
            DatabaseManager.logAccessEvent(userId, "Success", "Generate Report by Action Type", LoginRegistrationScreen.getIpAddress());
            JOptionPane.showMessageDialog(null, "Report generated by action type.");
        }
    }

    private static void handleGenerateReportByOutcome(int userId) {
        String outcome = JOptionPane.showInputDialog("Enter outcome to filter logs (Success/Failure):");
        if (outcome != null && !outcome.isEmpty()) {
            AdminFeatures.filterLogsByOutcome(outcome);
            DatabaseManager.logAccessEvent(userId, "Success", "Generate Report by Outcome", LoginRegistrationScreen.getIpAddress());
            JOptionPane.showMessageDialog(null, "Report generated by outcome.");
        }
    }

    private static void viewAllUsers() {
        StringBuilder userList = new StringBuilder();
        ResultSet rs = AdminFeatures.getAllUsers();
        try {
            while (rs.next()) {
                userList.append("ID: ").append(rs.getInt("id")).append(", Username: ").append(rs.getString("username")).append(", Role: ").append(rs.getString("role")).append("\n");
            }
            JOptionPane.showMessageDialog(null, userList.toString(), "All Users", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error retrieving users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
