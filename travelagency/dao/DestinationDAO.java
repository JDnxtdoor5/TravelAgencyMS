package com.travelagency.dao;

import com.travelagency.db.DBConnection;
import com.travelagency.model.Destination;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DestinationDAO {

    public boolean addDestination(Destination d) {
        String sql = "INSERT INTO destinations (name, country, description, best_season) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getCountry());
            ps.setString(3, d.getDescription());
            ps.setString(4, d.getBestSeason());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDestination(Destination d) {
        String sql = "UPDATE destinations SET name=?, country=?, description=?, best_season=? WHERE destination_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getName());
            ps.setString(2, d.getCountry());
            ps.setString(3, d.getDescription());
            ps.setString(4, d.getBestSeason());
            ps.setInt(5, d.getDestinationId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDestination(int destinationId) {
        String sql = "DELETE FROM destinations WHERE destination_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, destinationId);
            boolean deleted = ps.executeUpdate() > 0;
            if (deleted) {
                resyncAutoIncrement(conn);
            }
            return deleted;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Re-syncs the identity (auto-increment) counter to (current highest id + 1)
     * after a delete. Note: Derby will never let the counter go below the current
     * max id while rows still exist, so this only actually reclaims a number when
     * the row you just deleted was the one with the highest id (e.g. the last one
     * you added). Deleting a row in the middle will not "fill the gap" — that's
     * expected, standard database behavior, not a bug.
     */
    private void resyncAutoIncrement(Connection conn) throws SQLException {
        String sql = "SELECT COALESCE(MAX(destination_id), 0) + 1 AS next_id FROM destinations";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                int nextId = rs.getInt("next_id");
                try (Statement alter = conn.createStatement()) {
                    alter.executeUpdate("ALTER TABLE destinations ALTER COLUMN destination_id RESTART WITH " + nextId);
                }
            }
        }
    }

    public Destination getDestinationById(int id) {
        String sql = "SELECT * FROM destinations WHERE destination_id=?";
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

    public List<Destination> getAllDestinations() {
        List<Destination> list = new ArrayList<>();
        String sql = "SELECT * FROM destinations ORDER BY name";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Destination mapRow(ResultSet rs) throws SQLException {
        return new Destination(
                rs.getInt("destination_id"),
                rs.getString("name"),
                rs.getString("country"),
                rs.getString("description"),
                rs.getString("best_season")
        );
    }
}   