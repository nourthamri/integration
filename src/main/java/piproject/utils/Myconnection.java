package piproject.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Myconnection {

    // Change these based on your DB
    private static final String URL = "jdbc:mysql://localhost:3306/javafxdb";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection;

    private Myconnection() {
        // Private constructor to prevent instantiation
    }

    public static Connection getInstance() {
        if (connection == null) {
            try {
                // Optional: Load the JDBC driver (modern versions don't require this)
                Class.forName("com.mysql.cj.jdbc.Driver");

                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connected to the database");
            } catch (ClassNotFoundException | SQLException e) {
                System.err.println("❌ Database connection failed: " + e.getMessage());
            }
        }
        return connection;
    }
}
