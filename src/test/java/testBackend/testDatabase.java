package testBackend;
import java.sql.Connection;
import java.sql.DriverManager;

public class testDatabase {
        public static void main(String[] args) {
            String url = "jdbc:sqlite:data/downloadHistory.db"; // relative path
            try (Connection conn = DriverManager.getConnection(url)) {
                if (conn != null) {
                    System.out.println("✅ Connected to SQLite database!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
}
