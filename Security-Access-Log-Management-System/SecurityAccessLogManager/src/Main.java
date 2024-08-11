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

        String role;
        while (true) {
            System.out.print("Enter role for registration (User or Admin): ");
            role = scanner.nextLine().trim().toLowerCase();

            if (role.equals("user") || role.equals("admin")) {
                // Capitalize the first letter to store in a consistent format
                role = role.substring(0, 1).toUpperCase() + role.substring(1);
                break;
            } else {
                System.out.println("Invalid role entered. Please enter either 'User' or 'Admin'.");
            }
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
            System.out.println("Access granted");
        } else {
            System.out.println("Access denied");
        }

        scanner.close();
    }
}