package com.travelagency.dao;

import com.travelagency.db.DBConnection;
import com.travelagency.model.Booking;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    /** Generates a simple unique booking reference, e.g. BK-000123. */
    public String generateBookingRef() {
        String sql = "SELECT MAX(booking_id) AS max_id FROM bookings";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            int nextId = 1;
            if (rs.next()) {
                nextId = rs.getInt("max_id") + 1;
            }
            return String.format("BK-%06d", nextId);
        } catch (SQLException e) {
            e.printStackTrace();
            return "BK-" + System.currentTimeMillis();
        }
    }

    public boolean addBooking(Booking b) {
        String sql = "INSERT INTO bookings (booking_ref, customer_id, package_id, travel_date, " +
                     "num_travelers, status, total_amount) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, b.getBookingRef());
            ps.setInt(2, b.getCustomerId());
            ps.setInt(3, b.getPackageId());
            ps.setDate(4, b.getTravelDate());
            ps.setInt(5, b.getNumTravelers());
            ps.setString(6, b.getStatus());
            ps.setBigDecimal(7, b.getTotalAmount());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBookingStatus(int bookingId, String status) {
        String sql = "UPDATE bookings SET status=? WHERE booking_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBooking(int bookingId) {
        String sql = "DELETE FROM bookings WHERE booking_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, c.first_name || ' ' || c.last_name AS customer_name, " +
                     "p.package_name FROM bookings b " +
                     "JOIN customers c ON b.customer_id = c.customer_id " +
                     "JOIN packages p ON b.package_id = p.package_id " +
                     "ORDER BY b.booking_id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Booking> getBookingsByCustomer(int customerId) {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT b.*, c.first_name || ' ' || c.last_name AS customer_name, " +
                     "p.package_name FROM bookings b " +
                     "JOIN customers c ON b.customer_id = c.customer_id " +
                     "JOIN packages p ON b.package_id = p.package_id " +
                     "WHERE b.customer_id = ? ORDER BY b.booking_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking b = new Booking(
                rs.getInt("booking_id"),
                rs.getString("booking_ref"),
                rs.getInt("customer_id"),
                rs.getInt("package_id"),
                rs.getDate("travel_date"),
                rs.getInt("num_travelers"),
                rs.getString("status"),
                rs.getBigDecimal("total_amount")
        );
        b.setCustomerName(rs.getString("customer_name"));
        b.setPackageName(rs.getString("package_name"));
        return b;
    }
}