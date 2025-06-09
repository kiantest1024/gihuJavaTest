package com.login.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Database utility class for managing database connections and initialization
 */
public class DatabaseUtil {
    
    private static final String DB_PROPERTIES_FILE = "database.properties";
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;
    
    static {
        loadDatabaseProperties();
        initializeDatabase();
    }

    /**
     * Load database properties from configuration file
     */
    private static void loadDatabaseProperties() {
        Properties props = new Properties();
        try (InputStream input = DatabaseUtil.class.getClassLoader()
                .getResourceAsStream(DB_PROPERTIES_FILE)) {
            if (input != null) {
                props.load(input);
                dbUrl = props.getProperty("db.url", "jdbc:sqlite:login_system.db");
                dbUser = props.getProperty("db.user", "");
                dbPassword = props.getProperty("db.password", "");
            } else {
                // Use default SQLite database
                dbUrl = "jdbc:sqlite:login_system.db";
                dbUser = "";
                dbPassword = "";
            }
        } catch (IOException e) {
            System.err.println("Error loading database properties: " + e.getMessage());
            // Use default values
            dbUrl = "jdbc:sqlite:login_system.db";
            dbUser = "";
            dbPassword = "";
        }
    }

    /**
     * Get a database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found", e);
        }
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    /**
     * Initialize the database with required tables
     */
    private static void initializeDatabase() {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "email VARCHAR(100), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "last_login TIMESTAMP" +
                ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            System.out.println("Database initialized successfully");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Close database connection safely
     * @param connection the connection to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    /**
     * Test database connection
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}
