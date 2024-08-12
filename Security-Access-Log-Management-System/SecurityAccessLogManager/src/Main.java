import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Initialize the database
        DatabaseManager.connect();
        DatabaseManager.createTables();

        Scanner scanner = new Scanner(System.in);

        // Ask the user if they want to register or login
        System.out.println("Do you want to (1) Register or (2) Login?");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice == 1) {
            // User registration process
            System.out.print("Enter username for registration: ");
            String username = scanner.nextLine();

            System.out.print("Enter password for registration: ");
            String password = scanner.nextLine();

            System.out.print("Enter role for registration (User or Admin): ");
            String role = scanner.nextLine();

            AuthManager.registerUser(username, password, role);
            System.out.println("Registration successful. You can now log in.");

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
        } else {
            System.out.println("Invalid choice. Exiting.");
        }

        scanner.close();
    }
}
