import java.io.File;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Initialize the database
        DatabaseManager.connect();
        DatabaseManager.createTables();

        // Get the local IP address
        String ipAddress = "Unknown";
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            ipAddress = inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("Error getting IP address: " + e.getMessage());
        }

        Scanner scanner = new Scanner(System.in);
        int choice = 0;

        while (true) {
            System.out.println("Do you want to (1) Register or (2) Login?");
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Clear the buffer

                if (choice == 1 || choice == 2) {
                    break; // Exit the loop if a valid choice is made
                } else {
                    System.out.println("Invalid choice. Please enter 1 or 2.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the buffer
            }
        }

        if (choice == 1) {
            // Register a new user
            System.out.print("Enter username for registration: ");
            String username = scanner.nextLine();

            System.out.print("Enter password for registration: ");
            String password = scanner.nextLine();

            String role;
            while (true) {
                System.out.print("Enter role for registration (User or Admin): ");
                role = scanner.nextLine();

                if (role.equalsIgnoreCase("User") || role.equalsIgnoreCase("Admin")) {
                    break; // Valid role entered, exit loop
                } else {
                    System.out.println("Invalid role. Please enter 'User' or 'Admin'.");
                }
            }

            AuthManager.registerUser(username, password, role);
        } else if (choice == 2) {
            // User login process
            System.out.print("Enter username: ");
            String username = scanner.nextLine();

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            // Authenticate user and retrieve their role
            String role = AuthManager.authenticateUser(username, password);

            int userId = AuthManager.getUserId(username);

            if (role != null) {
                System.out.println("Access granted. Role: " + role);
                DatabaseManager.logAccessEvent(userId, "Success", "Login", ipAddress);

                // Role-Based Access Control (RBAC)
                if (role.equalsIgnoreCase("Admin")) {
                    System.out.println("Admin Access: You can view logs and manage users.");
                    AdminFeatures.viewAllLogs();
                    manageUsersMenu(scanner, username, ipAddress);  // Pass username and ipAddress

                } else if (role.equalsIgnoreCase("User")) {
                    System.out.println("User Access: Limited access to certain features.");
                    // Add user functionalities here

                } else {
                    System.out.println("Unknown role. Access restricted.");
                }

            } else {
                System.out.println("Access denied. Invalid username or password.");
                DatabaseManager.logAccessEvent(userId, "Failed", "Login", ipAddress); // Log failed attempt
            }
        }

        scanner.close();
    }
    

    // Function to display and handle user management options for Admins
    private static void manageUsersMenu(Scanner scanner, String username, String ipAddress) {
    int userId = AuthManager.getUserId(username); // Get the actual user ID for the logged-in user

    while (true) {
        System.out.println("\nUser Management:");
        System.out.println("1. Add User");
        System.out.println("2. Delete User");
        System.out.println("3. View All Users");
        System.out.println("4. View Access Logs");
        System.out.println("5. Generate Report by Date");
        System.out.println("6. Display Login Statistics");
        System.out.println("7. Exit User Management");
        System.out.print("Choose an option: ");

        int choice;
        try {
            choice = scanner.nextInt();
            scanner.nextLine(); // Clear the buffer

            switch (choice) {
                case 1:
                    System.out.print("Enter username for new user: ");
                    String newUsername = scanner.nextLine();

                    System.out.print("Enter password for new user: ");
                    String password = scanner.nextLine();

                    String role;
                    while (true) {
                        System.out.print("Enter role for new user (User or Admin): ");
                        role = scanner.nextLine();

                        if (role.equalsIgnoreCase("User") || role.equalsIgnoreCase("Admin")) {
                            break; // Valid role entered, exit loop
                        } else {
                            System.out.println("Invalid role. Please enter 'User' or 'Admin'.");
                        }
                    }

                    AuthManager.registerUser(newUsername, password, role);
                    System.out.println("User added successfully.");
                    DatabaseManager.logAccessEvent(userId, "Success", "Add User", ipAddress);
                    break;

                case 2:
                    System.out.print("Enter username of the user to delete: ");
                    String usernameToDelete = scanner.nextLine();

                    if (AdminFeatures.deleteUser(usernameToDelete)) {
                        System.out.println("User deleted successfully.");
                        DatabaseManager.logAccessEvent(userId, "Success", "Delete User", ipAddress);
                    } else {
                        System.out.println("User not found.");
                        DatabaseManager.logAccessEvent(userId, "Failed", "Delete User", ipAddress);
                    }
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
                    String startDate;
                    while (true) {
                        System.out.print("Enter start date (YYYY-MM-DD): ");
                        startDate = scanner.nextLine();
                        if (startDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                            String[] parts = startDate.split("-");
                            int month = Integer.parseInt(parts[1]);
                            int day = Integer.parseInt(parts[2]);
                
                            if (month >= 1 && month <= 12 && day >= 1 && day <= 31) {
                                break; // Valid date format and range
                            } else {
                                System.out.println("Invalid date. Month should be between 01-12 and day should be between 01-31.");
                            }
                        } else {
                            System.out.println("Invalid date format. Please enter the date in YYYY-MM-DD format.");
                        }
                    }
                
                    String endDate;
                    while (true) {
                        System.out.print("Enter end date (YYYY-MM-DD): ");
                        endDate = scanner.nextLine();
                        if (endDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
                            String[] parts = endDate.split("-");
                            int month = Integer.parseInt(parts[1]);
                            int day = Integer.parseInt(parts[2]);
                
                            if (month >= 1 && month <= 12 && day >= 1 && day <= 31) {
                                break; // Valid date format and range
                            } else {
                                System.out.println("Invalid date. Month should be between 01-12 and day should be between 01-31.");
                            }
                        } else {
                            System.out.println("Invalid date format. Please enter the date in YYYY-MM-DD format.");
                        }
                    }
                
                    // Display a summary of the report
                    System.out.println("Generating report for dates between " + startDate + " and " + endDate + "...");
                    AdminFeatures.generateReportByDate(startDate, endDate);
                
                    // Export the full report to a CSV file
                    System.out.print("Do you want to export the report to a CSV file? (yes/no): ");
String exportChoice = scanner.nextLine();
if (exportChoice.equalsIgnoreCase("yes")) {
    System.out.print("Enter the filename to save the report (e.g., report.csv): ");
    String filename = scanner.nextLine();

    List<Map<String, String>> resultList = AdminFeatures.filterLogsByDate(startDate, endDate);

    if (resultList.isEmpty()) {
        System.out.println("No data found to export.");
        DatabaseManager.logAccessEvent(userId, "Failed", "Export Report to CSV", ipAddress);
    } else {
        // Proceed with exporting to CSV
        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            writer.println("Log ID,User ID,Access Time,Status,Action,IP Address");
            for (Map<String, String> row : resultList) {
                writer.println(row.get("id") + "," +
                               row.get("user_id") + "," +
                               row.get("access_time") + "," +
                               row.get("status") + "," +
                               row.get("action") + "," +
                               row.get("ip_address"));
            }
            System.out.println("Report exported to " + filename);
            DatabaseManager.logAccessEvent(userId, "Success", "Export Report to CSV", ipAddress);
        } catch (Exception e) {
            System.out.println("Error exporting report: " + e.getMessage());
        }
    }




                    }
                
                    DatabaseManager.logAccessEvent(userId, "Success", "Generate Report by Date", ipAddress);
                    break;
                


                case 6:
                    AdminFeatures.displayLoginStatistics();
                    DatabaseManager.logAccessEvent(userId, "Success", "Display Login Statistics", ipAddress);
                    break;

                case 7:
                    System.out.println("Exiting User Management.");
                    DatabaseManager.logAccessEvent(userId, "Success", "Exit User Management", ipAddress);
                    return; // Exit the menu loop
                    
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 8.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear the buffer
        }
    }
}
}
