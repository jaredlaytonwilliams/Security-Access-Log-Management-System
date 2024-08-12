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

            if (role != null) {
                System.out.println("Access granted. Role: " + role);

                // Role-Based Access Control (RBAC)
                if (role.equalsIgnoreCase("Admin")) {
                    System.out.println("Admin Access: You can view logs and manage users.");

                    // Add code here for admin features like viewing logs, managing users, etc.

                } else if (role.equalsIgnoreCase("User")) {
                    System.out.println("User Access: Limited access to certain features.");

                    // Add code here for user features, restricted access

                } else {
                    System.out.println("Unknown role. Access restricted.");
                }

            } else {
                System.out.println("Access denied. Invalid username or password.");
            }
        }

        scanner.close();
    }
}
