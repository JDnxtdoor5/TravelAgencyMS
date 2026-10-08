package com.travelagency.dao;

import com.travelagency.db.DBConnection;
import com.travelagency.model.Payment;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    public boolean addPayment(Payment p) {
        String sql = "INSERT INTO payments (booking_id, amount_paid, payment_date, payment_method, status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getBookingId());
            ps.setBigDecimal(2, p.getAmountPaid());
            ps.setDate(3, p.getPaymentDate());
            ps.setString(4, p.getPaymentMethod());
            ps.setString(5, p.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Payment> getPaymentsByBooking(int bookingId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, b.booking_ref FROM payments p " +
                     "JOIN bookings b ON p.booking_id = b.booking_id " +
                     "WHERE p.booking_id = ? ORDER BY p.payment_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.*, b.booking_ref FROM payments p " +
                     "JOIN bookings b ON p.booking_id = b.booking_id " +
                     "ORDER BY p.payment_id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** total_amount (from the booking) minus sum of payments recorded so far. */
    public BigDecimal getOutstandingBalance(int bookingId) {
        String sql = "SELECT b.total_amount, COALESCE(SUM(p.amount_paid), 0) AS paid " +
                     "FROM bookings b LEFT JOIN payments p ON b.booking_id = p.booking_id " +
                     "WHERE b.booking_id = ? GROUP BY b.total_amount";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total_amount").subtract(rs.getBigDecimal("paid"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment p = new Payment(
                rs.getInt("payment_id"),
                rs.getInt("booking_id"),
                rs.getBigDecimal("amount_paid"),
                rs.getDate("payment_date"),
                rs.getString("payment_method"),
                rs.getString("status")
        );
        p.setBookingRef(rs.getString("booking_ref"));
        return p;
    }
}
