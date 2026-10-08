package com.travelagency.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Centralized JDBC connection handler for embedded Apache Derby.
 *
 * Unlike MySQL, Derby in embedded mode needs no separate server (no XAMPP,
 * no host/port, no username/password) — the database is just a folder that
 * gets created automatically, right inside the project directory, the first
 * time the app runs.
 */
public class DBConnection {

    /** Name of the database folder. Created automatically under the project root. */
    private static final String DB_NAME = "travel_agency_db";

    private static final String URL = "jdbc:derby:" + DB_NAME + ";create=true";

    private static Connection connection;

    private DBConnection() {
        // utility class
    }

    /**
     * Returns a live JDBC connection, creating the database and all tables
     * (via SchemaInitializer) the first time this is called.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
                connection = DriverManager.getConnection(URL);
                SchemaInitializer.initialize(connection);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Derby embedded driver not found. " +
                        "Make sure the derby dependency is on the classpath.", e);
            }
        }
        return connection;
    }

    /**
     * Closes the connection and shuts down the embedded Derby engine cleanly.
     * Call this when the application exits.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            // Derby's embedded shutdown always throws an SQLException on
            // success — this is expected Derby behavior, not an error.
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            // ignored: this is how Derby signals a successful shutdown
        }
    }

    /**
     * Quick manual test: run this class directly to confirm the database
     * initializes and connects correctly before running the full app.
     */
    public static void main(String[] args) {
        try {
            Connection conn = DBConnection.getConnection();
            System.out.println("\u2705 Connected to Derby successfully: " + conn.getCatalog());
            System.out.println("Database files created under: " + DB_NAME + "/ (inside your project folder)");
        } catch (SQLException e) {
            System.out.println("\u274C Connection failed:");
            e.printStackTrace();
        }
    }
}