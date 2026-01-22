package com.hospital.dao;

import com.hospital.model.PrescriptionItem;
import com.hospital.util.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for PrescriptionItem entities.
 * Handles SQL operations including CRUD.
 */
public class PrescriptionItemDAO {

    /**
     * Retrieves all prescription items from the database.
     * 
     * @return List of PrescriptionItem objects
     */
    public List<PrescriptionItem> getAllPrescriptionItems() {
        List<PrescriptionItem> items = new ArrayList<>();
        String sql = "SELECT * FROM PrescriptionItem ORDER BY prescription_item_id";

        try (Connection conn = DatabaseHelper.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(mapResultSetToPrescriptionItem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Retrieves a prescription item by ID.
     * 
     * @param id PrescriptionItem ID
     * @return PrescriptionItem object or null if not found
     */
    public PrescriptionItem getPrescriptionItemById(int id) {
        String sql = "SELECT * FROM PrescriptionItem WHERE prescription_item_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPrescriptionItem(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds a new prescription item to the database.
     * 
     * @param item PrescriptionItem object to add
     * @return true if successful
     */
    public boolean addPrescriptionItem(PrescriptionItem item) {
        String sql = "INSERT INTO PrescriptionItem (prescription_id, med_id, dosage) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, item.getPrescriptionId());
            pstmt.setInt(2, item.getMedId());
            pstmt.setString(3, item.getDosage());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing prescription item's details.
     * 
     * @param item PrescriptionItem object with updated details
     * @return true if successful
     */
    public boolean updatePrescriptionItem(PrescriptionItem item) {
        String sql = "UPDATE PrescriptionItem SET prescription_id=?, med_id=?, dosage=? WHERE prescription_item_id=?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, item.getPrescriptionId());
            pstmt.setInt(2, item.getMedId());
            pstmt.setString(3, item.getDosage());
            pstmt.setInt(4, item.getPrescriptionItemId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a prescription item by ID.
     * 
     * @param id PrescriptionItem ID
     * @return true if successful
     */
    public boolean deletePrescriptionItem(int id) {
        String sql = "DELETE FROM PrescriptionItem WHERE prescription_item_id=?";

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
     * Gets prescription items by prescription ID.
     * 
     * @param prescriptionId Prescription ID
     * @return List of items for the prescription
     */
    public List<PrescriptionItem> getItemsByPrescription(int prescriptionId) {
        List<PrescriptionItem> items = new ArrayList<>();
        String sql = "SELECT * FROM PrescriptionItem WHERE prescription_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, prescriptionId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                items.add(mapResultSetToPrescriptionItem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Gets prescription items by medication ID.
     * 
     * @param medId Medication ID
     * @return List of items containing the medication
     */
    public List<PrescriptionItem> getItemsByMedication(int medId) {
        List<PrescriptionItem> items = new ArrayList<>();
        String sql = "SELECT * FROM PrescriptionItem WHERE med_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, medId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                items.add(mapResultSetToPrescriptionItem(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    /**
     * Deletes all prescription items for a prescription.
     * 
     * @param prescriptionId Prescription ID
     * @return true if successful
     */
    public boolean deleteItemsByPrescription(int prescriptionId) {
        String sql = "DELETE FROM PrescriptionItem WHERE prescription_id=?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, prescriptionId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private PrescriptionItem mapResultSetToPrescriptionItem(ResultSet rs) throws SQLException {
        return new PrescriptionItem(
                rs.getInt("prescription_item_id"),
                rs.getInt("prescription_id"),
                rs.getInt("med_id"),
                rs.getString("dosage"));
    }
}
