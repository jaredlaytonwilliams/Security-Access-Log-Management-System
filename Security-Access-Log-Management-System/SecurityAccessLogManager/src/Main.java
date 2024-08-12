import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
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
        
            // Register user and get the user ID
            int userId = AuthManager.registerUser(username, password, role);
            if (userId != -1) {
                // Log the registration
                DatabaseManager.logAccessEvent(userId, "Success", "Registration", ipAddress);
        
                // Automatically log the user in
                System.out.println("Access granted. Role: " + role);
                DatabaseManager.logAccessEvent(userId, "Success", "Login", ipAddress);
        
                // Proceed with role-based access control (RBAC)
                if (role.equalsIgnoreCase("Admin")) {
                    System.out.println("Admin Access: You can view logs and manage users.");
                    AdminFeatures.viewAllLogs();
                    manageUsersMenu(scanner, username, ipAddress);
        
                } else if (role.equalsIgnoreCase("User")) {
                    System.out.println("User Access: Limited access to certain features.");
                    // Add user functionalities here
        
                } else {
                    System.out.println("Unknown role. Access restricted.");
                }
            }
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

        System.out.println("\nAccess Logs:");
        System.out.println("4. View All Access Logs");
        System.out.println("5. Generate Report by Date");
        System.out.println("6. Generate Report by User");
        System.out.println("7. Generate Report by Action Type");
        System.out.println("8. Generate Report by Outcome (Success/Failure)");

        System.out.println("\nStatistics:");
        System.out.println("9. Display Login Statistics");
        System.out.println("10. Display Most Active Users");

        System.out.println("\n11. Exit");

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
                    System.out.print("Enter start date (YYYY-MM-DD): ");
                    String startDate = scanner.nextLine();
                    System.out.print("Enter end date (YYYY-MM-DD): ");
                    String endDate = scanner.nextLine();
                    AdminFeatures.filterLogsByDate(startDate, endDate);
                    DatabaseManager.logAccessEvent(userId, "Success", "Generate Report by Date", ipAddress);
                    break;                

                case 6:
                    System.out.print("Enter username to filter logs: ");
                    String usernameFilter = scanner.nextLine();
                    AdminFeatures.filterLogsByUser(usernameFilter);
                    DatabaseManager.logAccessEvent(userId, "Success", "Generate Report by User", ipAddress);
                    break;

                case 7:
                    System.out.print("Enter action type to filter logs (e.g., Login, Logout): ");
                    String actionType = scanner.nextLine();
                    AdminFeatures.filterLogsByActionType(actionType);
                    DatabaseManager.logAccessEvent(userId, "Success", "Generate Report by Action Type", ipAddress);
                    break;

                case 8:
                    System.out.print("Enter outcome to filter logs (Success/Failure): ");
                    String outcome = scanner.nextLine();
                    AdminFeatures.filterLogsByOutcome(outcome);
                    DatabaseManager.logAccessEvent(userId, "Success", "Generate Report by Outcome", ipAddress);
                    break;

                    case 9:
                    AdminFeatures.displayLoginStatistics();
                    DatabaseManager.logAccessEvent(userId, "Success", "Display Login Statistics", ipAddress);
                    break;
                
                // Option 10: Display Most Active Users
                case 10:
                    AdminFeatures.displayMostActiveUsers();
                    DatabaseManager.logAccessEvent(userId, "Success", "Display Most Active Users", ipAddress);
                    break;

                case 11:
                    System.out.println("Exiting User Management.");
                    DatabaseManager.logAccessEvent(userId, "Success", "Exit User Management", ipAddress);
                    return; // Exit the menu loop

                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 11.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // Clear the buffer
        }
    }
}
}
