import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Initialize the database
        DatabaseManager.connect();
        DatabaseManager.createTables();

        Scanner scanner = new Scanner(System.in);

        // Register a new user
        System.out.print("Enter username for registration: ");
        String username = scanner.nextLine();

        System.out.print("Enter password for registration: ");
        String password = scanner.nextLine();

        System.out.print("Enter role for registration (User or Admin): ");
        String role = scanner.nextLine();
        // Ensure role is either "User" or "Admin"
        while (!role.equalsIgnoreCase("User") && !role.equalsIgnoreCase("Admin")) {
            System.out.print("Invalid role. Enter 'User' or 'Admin': ");
            role = scanner.nextLine();
        }

        AuthManager.registerUser(username, password, role);
        System.out.println("User registered successfully!");

        // Authenticate the user
        System.out.print("Enter username for authentication: ");
        String authUsername = scanner.nextLine();

        System.out.print("Enter password for authentication: ");
        String authPassword = scanner.nextLine();

        boolean isAuthenticated = AuthManager.authenticateUser(authUsername, authPassword);

        if (isAuthenticated) {
            String userRole = AuthManager.getUserRole(authUsername);

            System.out.println("Access granted. Your role: " + userRole);

            // Simulate access control based on role
            if (userRole.equalsIgnoreCase("Admin")) {
                // Admin has access to everything
                System.out.println("Welcome, Admin! You have full access to the system.");
                // Admin specific actions can be added here
                // For example: manageUsers(), viewAllLogs()
            } else if (userRole.equalsIgnoreCase("User")) {
                // User has limited access
                System.out.println("Welcome, User! You have limited access to the system.");
                // User specific actions can be added here
                // For example: viewOwnLogs(), changePassword()
            }
        } else {
            System.out.println("Access denied");
        }

        scanner.close();
    }
}
