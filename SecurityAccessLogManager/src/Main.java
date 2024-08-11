public class Main {
    public static void main(String[] args) {
        DatabaseManager.connect();
        DatabaseManager.createTables();   
        DatabaseManager.logAccessEvent(1, "Success"); }
}