package com.hospital.dao;

import com.hospital.model.Patient;
import com.hospital.util.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Patient entities.
 * Handles extensive SQL operations including CRUD and Search.
 */
public class PatientDAO {

    /**
     * Retrieves all patients from the database.
     * 
     * @return List of Patient objects
     */
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM Patient ORDER BY name";

        try (Connection conn = DatabaseHelper.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    /**
     * Adds a new patient to the database.
     * 
     * @param patient Patient object to add
     * @return true if successful
     */
    public boolean addPatient(Patient patient) {
        String sql = "INSERT INTO Patient (name, date_of_birth, contact) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patient.getName());
            pstmt.setDate(2, java.sql.Date.valueOf(patient.getDateOfBirth()));
            pstmt.setString(3, patient.getContact());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing patient's details.
     * 
     * @param patient Patient object with updated details
     * @return true if successful
     */
    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE Patient SET name=?, date_of_birth=?, contact=? WHERE patient_id=?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patient.getName());
            pstmt.setDate(2, java.sql.Date.valueOf(patient.getDateOfBirth()));
            pstmt.setString(3, patient.getContact());
            pstmt.setInt(4, patient.getPatientId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a patient by ID.
     * 
     * @param id Patient ID
     * @return true if successful
     */
    public boolean deletePatient(int id) {
        String sql = "DELETE FROM Patient WHERE patient_id=?";

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
     * Searches for patients by Name or ID.
     * Uses LIKE for name matching.
     * 
     * @param query Search string
     * @return List of matching patients
     */
    public List<Patient> searchPatients(String query) {
        List<Patient> patients = new ArrayList<>();
        // Search logic: If explicit ID (numeric), try that first, OR match names
        String sql = "SELECT * FROM Patient WHERE patient_id LIKE ? OR name LIKE ?";

        // Prepare query pattern
        String searchPattern = "%" + query + "%";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        return new Patient(
                rs.getInt("patient_id"),
                rs.getString("name"),
                rs.getDate("date_of_birth").toLocalDate(),
                rs.getString("contact"));
    }
}
