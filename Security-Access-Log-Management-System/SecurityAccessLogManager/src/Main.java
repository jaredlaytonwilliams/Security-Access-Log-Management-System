import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Initialize the database
        DatabaseManager.connect();
        DatabaseManager.createTables();

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

            int userId = AuthManager.getUserId(username); // Assuming you have a method to get user ID by username

            if (role != null) {
                System.out.println("Access granted. Role: " + role);
                DatabaseManager.logAccessEvent(userId, "Success", "Login", "127.0.0.1");

                // Role-Based Access Control (RBAC)
                if (role.equalsIgnoreCase("Admin")) {
                    System.out.println("Admin Access: You can view logs and manage users.");
                    AdminFeatures.viewAllLogs();
                    manageUsersMenu(scanner);

                } else if (role.equalsIgnoreCase("User")) {
                    System.out.println("User Access: Limited access to certain features.");
                    // Add user functionalities here

                } else {
                    System.out.println("Unknown role. Access restricted.");
                }

            } else {
                System.out.println("Access denied. Invalid username or password.");
                DatabaseManager.logAccessEvent(userId, "Failed", "Login", "127.0.0.1"); // Log failed attempt
            }
        }

        scanner.close();
    }

    // Function to display and handle user management options for Admins
    private static void manageUsersMenu(Scanner scanner) {
        while (true) {
            System.out.println("\nUser Management:");
            System.out.println("1. Add User");
            System.out.println("2. Delete User");
            System.out.println("3. View All Users");
            System.out.println("4. Exit User Management");
            System.out.print("Choose an option: ");
            
            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Clear the buffer

                switch (choice) {
                    case 1:
                        System.out.print("Enter username for new user: ");
                        String username = scanner.nextLine();

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

                        AuthManager.registerUser(username, password, role);
                        System.out.println("User added successfully.");
                        break;

                    case 2:
                        System.out.print("Enter username of the user to delete: ");
                        String usernameToDelete = scanner.nextLine();

                        if (AdminFeatures.deleteUser(usernameToDelete)) {
                            System.out.println("User deleted successfully.");
                        } else {
                            System.out.println("User not found.");
                        }
                        break;

                    case 3:
                        AdminFeatures.viewAllUsers();
                        break;

                    case 4:
                        System.out.println("Exiting User Management.");
                        return; // Exit the menu loop

                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the buffer
            }
        }
    }
}
