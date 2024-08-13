import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        initializeDatabase();
        String ipAddress = getLocalIpAddress();

        Scanner scanner = new Scanner(System.in);
        int choice = getUserChoice(scanner, "Do you want to (1) Register or (2) Login?", 1, 2);

        if (choice == 1) {
            handleUserRegistration(scanner, ipAddress);
        } else if (choice == 2) {
            handleUserLogin(scanner, ipAddress);
        }

        scanner.close();
    }

    private static void initializeDatabase() {
        DatabaseManager.connect();
        DatabaseManager.createTables();
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

    private static int getUserChoice(Scanner scanner, String message, int... validChoices) {
        int choice = 0;
        while (true) {
            System.out.println(message);
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Clear the buffer

                for (int validChoice : validChoices) {
                    if (choice == validChoice) {
                        return choice;
                    }
                }
                System.out.println("Invalid choice. Please enter " + formatChoices(validChoices) + ".");
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the buffer
            }
        }
    }

    private static String formatChoices(int... validChoices) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < validChoices.length; i++) {
            sb.append(validChoices[i]);
            if (i < validChoices.length - 1) {
                sb.append(" or ");
            }
        }
        return sb.toString();
    }

    private static void handleUserRegistration(Scanner scanner, String ipAddress) {
        String username = getInput(scanner, "Enter username for registration: ");
        String password = getInput(scanner, "Enter password for registration: ");
        String role = getRole(scanner);

        int userId = AuthManager.registerUser(username, password, role);
        if (userId != -1) {
            DatabaseManager.logAccessEvent(userId, "Success", "Registration", ipAddress);
            handleUserLoginFlow(scanner, username, role, userId, ipAddress);
        }
    }

    private static void handleUserLogin(Scanner scanner, String ipAddress) {
        String username = getInput(scanner, "Enter username: ");
        String password = getInput(scanner, "Enter password: ");
        String role = AuthManager.authenticateUser(username, password);

        int userId = AuthManager.getUserId(username);

        if (role != null) {
            DatabaseManager.logAccessEvent(userId, "Success", "Login", ipAddress);
            handleUserLoginFlow(scanner, username, role, userId, ipAddress);
        } else {
            System.out.println("Access denied. Invalid username or password.");
            DatabaseManager.logAccessEvent(userId, "Failed", "Login", ipAddress);
        }
    }

    private static void handleUserLoginFlow(Scanner scanner, String username, String role, int userId, String ipAddress) {
        System.out.println("Access granted. Role: " + role);
        if (role.equalsIgnoreCase("Admin")) {
            manageUsersMenu(scanner, username, ipAddress);
        } else if (role.equalsIgnoreCase("User")) {
            manageUserOptions(scanner, username);
        } else {
            System.out.println("Unknown role. Access restricted.");
        }
    }

    private static void manageUserOptions(Scanner scanner, String username) {
        while (true) {
            int userChoice = getUserChoice(scanner, "\n1. View My Logs\n2. View My Login Statistics\n3. Exit", 1, 2, 3);
            switch (userChoice) {
                case 1:
                    UserFeatures.viewOwnLogs(username);
                    break;
                case 2:
                    UserFeatures.viewLoginStatistics(username);
                    break;
                case 3:
                    System.out.println("Exiting.");
                    return;
            }
        }
    }

    private static void manageUsersMenu(Scanner scanner, String username, String ipAddress) {
        int userId = AuthManager.getUserId(username);

        while (true) {
            int choice = getUserChoice(scanner,
                "\nUser Management:\n1. Add User\n2. Delete User\n3. View All Users" +
                "\n\nAccess Logs:\n4. View All Access Logs\n5. Generate Report by Date\n6. Generate Report by User" +
                "\n7. Generate Report by Action Type\n8. Generate Report by Outcome (Success/Failure)" +
                "\n\nStatistics:\n9. Display Login Statistics\n10. Display Most Active Users" +
                "\n\n11. Exit", 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);

            switch (choice) {
                case 1:
                    handleAddUser(scanner, userId, ipAddress);
                    break;
                case 2:
                    handleDeleteUser(scanner, userId, ipAddress);
                    break;
                case 3:
                    AdminFeatures.viewAllUsers();
                    DatabaseManager.logAccessEvent(userId, "Success", "View All Users", ipAddress);
                    break;
                case 4:
                    AdminFeatures.viewAllLogs();
                    DatabaseManager.logAccessEvent(userId, "Success", "View Access Logs", ipAddress);
                    break;
                case 5:
                    handleGenerateReportByDate(scanner, userId, ipAddress);
                    break;
                case 6:
                    handleGenerateReportByUser(scanner, userId, ipAddress);
                    break;
                case 7:
                    handleGenerateReportByActionType(scanner, userId, ipAddress);
                    break;
                case 8:
                    handleGenerateReportByOutcome(scanner, userId, ipAddress);
                    break;
                case 9:
                    AdminFeatures.displayLoginStatistics();
                    DatabaseManager.logAccessEvent(userId, "Success", "Display Login Statistics", ipAddress);
                    break;
                case 10:
                    AdminFeatures.displayMostActiveUsers();
                    DatabaseManager.logAccessEvent(userId, "Success", "Display Most Active Users", ipAddress);
                    break;
                case 11:
                    System.out.println("Exiting User Management.");
                    DatabaseManager.logAccessEvent(userId, "Success", "Exit User Management", ipAddress);
                    return;
            }
        }
    }

    private static void handleAddUser(Scanner scanner, int userId, String ipAddress) {
        String newUsername = getInput(scanner, "Enter username for new user: ");
        String password = getInput(scanner, "Enter password for new user: ");
        String role = getRole(scanner);

        AuthManager.registerUser(newUsername, password, role);
        System.out.println("User added successfully.");
        DatabaseManager.logAccessEvent(userId, "Success", "Add User", ipAddress);
    }

    private static void handleDeleteUser(Scanner scanner, int userId, String ipAddress) {
        String usernameToDelete = getInput(scanner, "Enter username of the user to delete: ");
        if (AdminFeatures.deleteUser(usernameToDelete)) {
            System.out.println("User deleted successfully.");
            DatabaseManager.logAccessEvent(userId, "Success", "Delete User", ipAddress);
        } else {
            System.out.println("User not found.");
            DatabaseManager.logAccessEvent(userId, "Failed", "Delete User", ipAddress);
        }
    }

    private static void handleGenerateReportByDate(Scanner scanner, int userId, String ipAddress) {
        String startDate = getInput(scanner, "Enter start date (YYYY-MM-DD): ");
        String endDate = getInput(scanner, "Enter end date (YYYY-MM-DD): ");
        AdminFeatures.filterLogsByDate(startDate, endDate);
        DatabaseManager.logAccessEvent(userId, "Success", "Generate Report by Date", ipAddress);
    }

    private static void handleGenerateReportByUser(Scanner scanner, int userId, String ipAddress) {
        String usernameFilter = getInput(scanner, "Enter username to filter logs: ");
        AdminFeatures.filterLogsByUser(usernameFilter);
        DatabaseManager.logAccessEvent(userId, "Success", "Generate Report by User", ipAddress);
    }

    private static void handleGenerateReportByActionType(Scanner scanner, int userId, String ipAddress) {
        String actionType = getInput(scanner, "Enter action type to filter logs (e.g., Login, Logout): ");
        AdminFeatures.filterLogsByActionType(actionType);
        DatabaseManager.logAccessEvent(userId, "Success", "Generate Report by Action Type", ipAddress);
    }

    private static void handleGenerateReportByOutcome(Scanner scanner, int userId, String ipAddress) {
        String outcome = getInput(scanner, "Enter outcome to filter logs (Success/Failure): ");
        AdminFeatures.filterLogsByOutcome(outcome);
        DatabaseManager.logAccessEvent(userId, "Success", "Generate Report by Outcome", ipAddress);
    }

    private static String getInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static String getRole(Scanner scanner) {
        String role;
        while (true) {
            System.out.print("Enter role for registration (User or Admin): ");
            role = scanner.nextLine();
            if (role.equalsIgnoreCase("User") || role.equalsIgnoreCase("Admin")) {
                return role;
            } else {
                System.out.println("Invalid role. Please enter 'User' or 'Admin'.");
            }
        }
    }
}
