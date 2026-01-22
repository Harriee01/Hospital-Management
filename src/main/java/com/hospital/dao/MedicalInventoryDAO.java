package com.hospital.dao;

import com.hospital.model.MedicalInventory;
import com.hospital.util.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for MedicalInventory entities.
 * Handles SQL operations including CRUD and Search.
 */
public class MedicalInventoryDAO {

    /**
     * Retrieves all medical inventory items from the database.
     * 
     * @return List of MedicalInventory objects
     */
    public List<MedicalInventory> getAllMedicalInventory() {
        List<MedicalInventory> items = new ArrayList<>();
        String sql = "SELECT * FROM MedicalInventory ORDER BY name";

        try (Connection conn = DatabaseHelper.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(mapResultSetToMedicalInventory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Retrieves a medical inventory item by ID.
     * 
     * @param id Medical inventory ID
     * @return MedicalInventory object or null if not found
     */
    public MedicalInventory getMedicalInventoryById(int id) {
        String sql = "SELECT * FROM MedicalInventory WHERE med_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToMedicalInventory(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds a new medical inventory item to the database.
     * 
     * @param item MedicalInventory object to add
     * @return true if successful
     */
    public boolean addMedicalInventory(MedicalInventory item) {
        String sql = "INSERT INTO MedicalInventory (name, quantity, expiry_date) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getName());
            pstmt.setInt(2, item.getQuantity());
            pstmt.setDate(3, item.getExpiryDate() != null ? java.sql.Date.valueOf(item.getExpiryDate()) : null);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing medical inventory item's details.
     * 
     * @param item MedicalInventory object with updated details
     * @return true if successful
     */
    public boolean updateMedicalInventory(MedicalInventory item) {
        String sql = "UPDATE MedicalInventory SET name=?, quantity=?, expiry_date=? WHERE med_id=?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getName());
            pstmt.setInt(2, item.getQuantity());
            pstmt.setDate(3, item.getExpiryDate() != null ? java.sql.Date.valueOf(item.getExpiryDate()) : null);
            pstmt.setInt(4, item.getMedId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a medical inventory item by ID.
     * 
     * @param id Medical inventory ID
     * @return true if successful
     */
    public boolean deleteMedicalInventory(int id) {
        String sql = "DELETE FROM MedicalInventory WHERE med_id=?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Searches for medical inventory items by name.
     * 
     * @param query Search string
     * @return List of matching items
     */
    public List<MedicalInventory> searchMedicalInventory(String query) {
        List<MedicalInventory> items = new ArrayList<>();
        String sql = "SELECT * FROM MedicalInventory WHERE med_id LIKE ? OR name LIKE ?";

        String searchPattern = "%" + query + "%";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                items.add(mapResultSetToMedicalInventory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Updates the quantity of a medical inventory item.
     * 
     * @param id       Medical inventory ID
     * @param quantity New quantity
     * @return true if successful
     */
    public boolean updateQuantity(int id, int quantity) {
        String sql = "UPDATE MedicalInventory SET quantity=? WHERE med_id=?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantity);
            pstmt.setInt(2, id);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets items with low stock (quantity below threshold).
     * 
     * @param threshold Minimum quantity threshold
     * @return List of low stock items
     */
    public List<MedicalInventory> getLowStockItems(int threshold) {
        List<MedicalInventory> items = new ArrayList<>();
        String sql = "SELECT * FROM MedicalInventory WHERE quantity < ? ORDER BY quantity";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, threshold);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                items.add(mapResultSetToMedicalInventory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Gets expired items.
     * 
     * @return List of expired items
     */
    public List<MedicalInventory> getExpiredItems() {
        List<MedicalInventory> items = new ArrayList<>();
        String sql = "SELECT * FROM MedicalInventory WHERE expiry_date < CURDATE() ORDER BY expiry_date";

        try (Connection conn = DatabaseHelper.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(mapResultSetToMedicalInventory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    private MedicalInventory mapResultSetToMedicalInventory(ResultSet rs) throws SQLException {
        java.sql.Date expiryDate = rs.getDate("expiry_date");
        return new MedicalInventory(
                rs.getInt("med_id"),
                rs.getString("name"),
                rs.getInt("quantity"),
                expiryDate != null ? expiryDate.toLocalDate() : null);
    }
}
