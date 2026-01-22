package com.hospital.dao;

import com.hospital.model.Prescription;
import com.hospital.util.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Prescription entities.
 * Handles SQL operations including CRUD and Search.
 */
public class PrescriptionDAO {

    /**
     * Retrieves all prescriptions from the database.
     * 
     * @return List of Prescription objects
     */
    public List<Prescription> getAllPrescriptions() {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT * FROM Prescription ORDER BY prescription_date DESC";

        try (Connection conn = DatabaseHelper.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                prescriptions.add(mapResultSetToPrescription(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prescriptions;
    }

    /**
     * Retrieves a prescription by ID.
     * 
     * @param id Prescription ID
     * @return Prescription object or null if not found
     */
    public Prescription getPrescriptionById(int id) {
        String sql = "SELECT * FROM Prescription WHERE prescription_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPrescription(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds a new prescription to the database.
     * 
     * @param prescription Prescription object to add
     * @return generated prescription ID, or -1 if failed
     */
    public int addPrescription(Prescription prescription) {
        String sql = "INSERT INTO Prescription (patient_id, doctor_id, prescription_date) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, prescription.getPatientId());
            pstmt.setInt(2, prescription.getDoctorId());
            pstmt.setDate(3, java.sql.Date.valueOf(prescription.getPrescriptionDate()));

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Updates an existing prescription's details.
     * 
     * @param prescription Prescription object with updated details
     * @return true if successful
     */
    public boolean updatePrescription(Prescription prescription) {
        String sql = "UPDATE Prescription SET patient_id=?, doctor_id=?, prescription_date=? WHERE prescription_id=?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, prescription.getPatientId());
            pstmt.setInt(2, prescription.getDoctorId());
            pstmt.setDate(3, java.sql.Date.valueOf(prescription.getPrescriptionDate()));
            pstmt.setInt(4, prescription.getPrescriptionId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a prescription by ID.
     * 
     * @param id Prescription ID
     * @return true if successful
     */
    public boolean deletePrescription(int id) {
        String sql = "DELETE FROM Prescription WHERE prescription_id=?";

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
     * Gets prescriptions by patient ID.
     * 
     * @param patientId Patient ID
     * @return List of prescriptions for the patient
     */
    public List<Prescription> getPrescriptionsByPatient(int patientId) {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT * FROM Prescription WHERE patient_id = ? ORDER BY prescription_date DESC";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                prescriptions.add(mapResultSetToPrescription(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prescriptions;
    }

    /**
     * Gets prescriptions by doctor ID.
     * 
     * @param doctorId Doctor ID
     * @return List of prescriptions by the doctor
     */
    public List<Prescription> getPrescriptionsByDoctor(int doctorId) {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT * FROM Prescription WHERE doctor_id = ? ORDER BY prescription_date DESC";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                prescriptions.add(mapResultSetToPrescription(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prescriptions;
    }

    /**
     * Searches for prescriptions by ID.
     * 
     * @param query Search string
     * @return List of matching prescriptions
     */
    public List<Prescription> searchPrescriptions(String query) {
        List<Prescription> prescriptions = new ArrayList<>();
        String sql = "SELECT * FROM Prescription WHERE prescription_id LIKE ?";

        String searchPattern = "%" + query + "%";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                prescriptions.add(mapResultSetToPrescription(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prescriptions;
    }

    private Prescription mapResultSetToPrescription(ResultSet rs) throws SQLException {
        return new Prescription(
                rs.getInt("prescription_id"),
                rs.getInt("patient_id"),
                rs.getInt("doctor_id"),
                rs.getDate("prescription_date").toLocalDate());
    }
}
