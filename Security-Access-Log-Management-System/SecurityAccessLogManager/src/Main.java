public class Main {

    public static void main(String[] args) {
        // Initialize the database
        DatabaseManager.connect();
        DatabaseManager.createTables();

        // Show the login/registration screen
        LoginRegistrationScreen.show();
    }
}
