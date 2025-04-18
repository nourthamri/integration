package tn.esprit.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    private static MyConnection instance;
    private Connection connection;
    private final String URL = "jdbc:mysql://localhost:3306/java_bd?autoReconnect=true&useSSL=false";
    private final String USER = "root";
    private final String PSW = "";

    // Private constructor to enforce Singleton pattern
    private MyConnection() {
        connect();
    }

    // Singleton instance retrieval
    public static synchronized MyConnection getInstance() {
        if (instance == null) {
            instance = new MyConnection();
        }
        return instance;
    }

    // Establish a new connection
    private void connect() {
        try {
            connection = DriverManager.getConnection(URL, USER, PSW);
            System.out.println("✅ Connection established");
        } catch (SQLException e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
        }
    }

    // Get a valid connection
    public synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                System.out.println("🔄 Reconnecting to database...");
                connect();  // Reconnect if the connection is closed or invalid
            }
        } catch (SQLException e) {
            System.err.println("⚠ Error checking connection status: " + e.getMessage());
            // Optionally, you could reconnect here if needed
        }
        return connection;
    }


    // Close the connection
    public synchronized void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("🔌 Connection closed");
            } catch (SQLException e) {
                System.err.println("⚠ Error closing connection: " + e.getMessage());
            }
        }
    }
}
