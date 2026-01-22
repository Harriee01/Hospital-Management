package com.hospital.dao;

import com.hospital.model.Patient;
import com.hospital.util.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Patient entities.
 * Handles extensive SQL operations including CRUD and Search.
 * Implements IPatientDAO interface following SOLID principles.
 */
public class PatientDAO implements PatientRepository {

    /**
     * Retrieves all patients from the database.
     * 
     * @return List of Patient objects
     */
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM Patient ORDER BY name";

        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    patients.add(mapResultSetToPatient(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseHelper.releaseConnection(conn);
            }
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

        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, patient.getName());
                pstmt.setDate(2, java.sql.Date.valueOf(patient.getDateOfBirth()));
                pstmt.setString(3, patient.getContact());

                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                DatabaseHelper.releaseConnection(conn);
            }
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

        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, patient.getName());
                pstmt.setDate(2, java.sql.Date.valueOf(patient.getDateOfBirth()));
                pstmt.setString(3, patient.getContact());
                pstmt.setInt(4, patient.getPatientId());

                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                DatabaseHelper.releaseConnection(conn);
            }
        }
    }

    /**
     * Deletes a patient by ID.
     * Handles foreign key constraints by deleting related records first.
     * 
     * @param id Patient ID
     * @return true if successful
     */
    public boolean deletePatient(int id) {
        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Delete related records in order (respecting foreign key constraints)
            // 1. Delete PrescriptionItems for prescriptions of this patient
            PrescriptionItemDAO prescriptionItemDAO = new PrescriptionItemDAO();
            PrescriptionDAO prescriptionDAO = new PrescriptionDAO();
            List<com.hospital.model.Prescription> prescriptions = prescriptionDAO.getPrescriptionsByPatient(id);
            for (com.hospital.model.Prescription prescription : prescriptions) {
                prescriptionItemDAO.deleteItemsByPrescription(prescription.getPrescriptionId());
            }
            
            // 2. Delete Prescriptions
            String deletePrescriptions = "DELETE FROM Prescription WHERE patient_id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(deletePrescriptions)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            
            // 3. Delete Appointments
            String deleteAppointments = "DELETE FROM Appointment WHERE patient_id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteAppointments)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            
            // 4. Delete PatientFeedback
            String deleteFeedback = "DELETE FROM PatientFeedback WHERE patient_id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteFeedback)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }
            
            // 5. Finally, delete the Patient
            String deletePatient = "DELETE FROM Patient WHERE patient_id=?";
            try (PreparedStatement pstmt = conn.prepareStatement(deletePatient)) {
                pstmt.setInt(1, id);
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    conn.commit(); // Commit transaction
                    return true;
                } else {
                    conn.rollback(); // Rollback if no rows affected
                    return false;
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    DatabaseHelper.releaseConnection(conn);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Retrieves a patient by ID.
     * 
     * @param id Patient ID
     * @return Patient object or null if not found
     */
    public Patient getPatientById(int id) {
        String sql = "SELECT * FROM Patient WHERE patient_id = ?";

        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseHelper.releaseConnection(conn);
            }
        }
        return null;
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

        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, searchPattern);
                pstmt.setString(2, searchPattern);

                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    patients.add(mapResultSetToPatient(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseHelper.releaseConnection(conn);
            }
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
