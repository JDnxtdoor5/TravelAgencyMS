package com.travelagency.dao;

import com.travelagency.db.DBConnection;
import com.travelagency.model.TravelPackage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PackageDAO {

    public boolean addPackage(TravelPackage p) {
        String sql = "INSERT INTO packages (package_name, destination_id, duration_days, price, inclusions, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getPackageName());
            ps.setInt(2, p.getDestinationId());
            ps.setInt(3, p.getDurationDays());
            ps.setBigDecimal(4, p.getPrice());
            ps.setString(5, p.getInclusions());
            ps.setBoolean(6, p.isActive());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePackage(TravelPackage p) {
        String sql = "UPDATE packages SET package_name=?, destination_id=?, duration_days=?, price=?, " +
                     "inclusions=?, is_active=? WHERE package_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getPackageName());
            ps.setInt(2, p.getDestinationId());
            ps.setInt(3, p.getDurationDays());
            ps.setBigDecimal(4, p.getPrice());
            ps.setString(5, p.getInclusions());
            ps.setBoolean(6, p.isActive());
            ps.setInt(7, p.getPackageId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePackage(int packageId) {
        String sql = "DELETE FROM packages WHERE package_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, packageId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public TravelPackage getPackageById(int id) {
        String sql = "SELECT p.*, d.name AS destination_name FROM packages p " +
                     "JOIN destinations d ON p.destination_id = d.destination_id " +
                     "WHERE p.package_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<TravelPackage> getAllPackages() {
        List<TravelPackage> list = new ArrayList<>();
        String sql = "SELECT p.*, d.name AS destination_name FROM packages p " +
                     "JOIN destinations d ON p.destination_id = d.destination_id " +
                     "ORDER BY p.package_id DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Only packages flagged active — for booking dropdowns. */
    public List<TravelPackage> getActivePackages() {
        List<TravelPackage> list = new ArrayList<>();
        String sql = "SELECT p.*, d.name AS destination_name FROM packages p " +
                     "JOIN destinations d ON p.destination_id = d.destination_id " +
                     "WHERE p.is_active = TRUE ORDER BY p.package_name";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private TravelPackage mapRow(ResultSet rs) throws SQLException {
        TravelPackage p = new TravelPackage(
                rs.getInt("package_id"),
                rs.getString("package_name"),
                rs.getInt("destination_id"),
                rs.getInt("duration_days"),
                rs.getBigDecimal("price"),
                rs.getString("inclusions"),
                rs.getBoolean("is_active")
        );
        p.setDestinationName(rs.getString("destination_name"));
        return p;
    }
}
