package com.travelagency.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creates every table (and the default admin login) the first time the app
 * connects to a fresh Derby database. Unlike MySQL/phpMyAdmin, there is no
 * manual "run this SQL file" step — this runs automatically inside
 * DBConnection.getConnection() and does nothing on every run after the
 * first, since it checks whether the USERS table already exists.
 */
public class SchemaInitializer {

    private SchemaInitializer() {}

    public static void initialize(Connection conn) throws SQLException {
        if (tablesAlreadyExist(conn)) {
            return;
        }

        try (Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE TABLE users (" +
                    "user_id INT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "full_name VARCHAR(100), " +
                    "role VARCHAR(10) NOT NULL DEFAULT 'STAFF' CHECK (role IN ('ADMIN','STAFF')), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            st.executeUpdate("CREATE TABLE customers (" +
                    "customer_id INT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, " +
                    "first_name VARCHAR(50) NOT NULL, " +
                    "last_name VARCHAR(50) NOT NULL, " +
                    "email VARCHAR(100), " +
                    "phone VARCHAR(20), " +
                    "address VARCHAR(255), " +
                    "id_number VARCHAR(50), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            st.executeUpdate("CREATE TABLE destinations (" +
                    "destination_id INT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "country VARCHAR(100), " +
                    "description VARCHAR(2000), " +
                    "best_season VARCHAR(50))");

            st.executeUpdate("CREATE TABLE packages (" +
                    "package_id INT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, " +
                    "package_name VARCHAR(100) NOT NULL, " +
                    "destination_id INT NOT NULL, " +
                    "duration_days INT NOT NULL, " +
                    "price DECIMAL(10,2) NOT NULL, " +
                    "inclusions VARCHAR(255), " +
                    "is_active BOOLEAN DEFAULT TRUE, " +
                    "CONSTRAINT fk_package_destination FOREIGN KEY (destination_id) " +
                    "REFERENCES destinations(destination_id) ON DELETE CASCADE)");

            st.executeUpdate("CREATE TABLE bookings (" +
                    "booking_id INT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, " +
                    "booking_ref VARCHAR(20) UNIQUE NOT NULL, " +
                    "customer_id INT NOT NULL, " +
                    "package_id INT NOT NULL, " +
                    "travel_date DATE NOT NULL, " +
                    "num_travelers INT NOT NULL DEFAULT 1, " +
                    "status VARCHAR(20) DEFAULT 'PENDING' " +
                    "CHECK (status IN ('PENDING','CONFIRMED','CANCELLED','COMPLETED')), " +
                    "total_amount DECIMAL(10,2) NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "CONSTRAINT fk_booking_customer FOREIGN KEY (customer_id) " +
                    "REFERENCES customers(customer_id) ON DELETE CASCADE, " +
                    "CONSTRAINT fk_booking_package FOREIGN KEY (package_id) " +
                    "REFERENCES packages(package_id))");

            st.executeUpdate("CREATE TABLE payments (" +
                    "payment_id INT GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY, " +
                    "booking_id INT NOT NULL, " +
                    "amount_paid DECIMAL(10,2) NOT NULL, " +
                    "payment_date DATE NOT NULL, " +
                    "payment_method VARCHAR(20) DEFAULT 'CASH' " +
                    "CHECK (payment_method IN ('CASH','CARD','BANK_TRANSFER','GCASH','OTHER')), " +
                    "status VARCHAR(20) DEFAULT 'PAID' CHECK (status IN ('PAID','PARTIAL','REFUNDED')), " +
                    "CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) " +
                    "REFERENCES bookings(booking_id) ON DELETE CASCADE)");

            st.executeUpdate("INSERT INTO users (username, password, full_name, role) " +
                    "VALUES ('admin', 'admin123', 'System Administrator', 'ADMIN')");

            System.out.println("Derby database initialized: all tables created, default admin seeded.");
        }
    }

    /** Checks the database metadata for the USERS table rather than guessing via a SELECT. */
    private static boolean tablesAlreadyExist(Connection conn) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getTables(null, null, "USERS", new String[]{"TABLE"})) {
            return rs.next();
        }
    }
}