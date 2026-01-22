package com.hospital.dao;

import com.hospital.model.Appointment;
import com.hospital.util.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Appointment entities.
 * Handles SQL operations including CRUD and Search.
 */
public class AppointmentDAO {

    /**
     * Retrieves all appointments from the database.
     * 
     * @return List of Appointment objects
     */
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM Appointment ORDER BY appointment_date DESC";

        try (Connection conn = DatabaseHelper.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Retrieves an appointment by ID.
     * 
     * @param id Appointment ID
     * @return Appointment object or null if not found
     */
    public Appointment getAppointmentById(int id) {
        String sql = "SELECT * FROM Appointment WHERE appointment_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAppointment(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds a new appointment to the database.
     * 
     * @param appointment Appointment object to add
     * @return true if successful
     */
    public boolean addAppointment(Appointment appointment) {
        String sql = "INSERT INTO Appointment (patient_id, doctor_id, status, appointment_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, appointment.getPatientId());
            pstmt.setInt(2, appointment.getDoctorId());
            pstmt.setString(3, appointment.getStatus());
            pstmt.setTimestamp(4, Timestamp.valueOf(appointment.getAppointmentDate()));

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing appointment's details.
     * 
     * @param appointment Appointment object with updated details
     * @return true if successful
     */
    public boolean updateAppointment(Appointment appointment) {
        String sql = "UPDATE Appointment SET patient_id=?, doctor_id=?, status=?, appointment_date=? WHERE appointment_id=?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, appointment.getPatientId());
            pstmt.setInt(2, appointment.getDoctorId());
            pstmt.setString(3, appointment.getStatus());
            pstmt.setTimestamp(4, Timestamp.valueOf(appointment.getAppointmentDate()));
            pstmt.setInt(5, appointment.getAppointmentId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes an appointment by ID.
     * 
     * @param id Appointment ID
     * @return true if successful
     */
    public boolean deleteAppointment(int id) {
        String sql = "DELETE FROM Appointment WHERE appointment_id=?";

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
     * Searches for appointments by status.
     * 
     * @param query Search string (status)
     * @return List of matching appointments
     */
    public List<Appointment> searchAppointments(String query) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM Appointment WHERE appointment_id LIKE ? OR status LIKE ?";

        String searchPattern = "%" + query + "%";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Gets appointments by patient ID.
     * 
     * @param patientId Patient ID
     * @return List of appointments for the patient
     */
    public List<Appointment> getAppointmentsByPatient(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM Appointment WHERE patient_id = ? ORDER BY appointment_date DESC";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Gets appointments by doctor ID.
     * 
     * @param doctorId Doctor ID
     * @return List of appointments for the doctor
     */
    public List<Appointment> getAppointmentsByDoctor(int doctorId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM Appointment WHERE doctor_id = ? ORDER BY appointment_date DESC";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    /**
     * Gets appointments by status.
     * 
     * @param status Appointment status
     * @return List of appointments with the given status
     */
    public List<Appointment> getAppointmentsByStatus(String status) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM Appointment WHERE status = ? ORDER BY appointment_date DESC";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        return new Appointment(
                rs.getInt("appointment_id"),
                rs.getInt("patient_id"),
                rs.getInt("doctor_id"),
                rs.getString("status"),
                rs.getTimestamp("appointment_date").toLocalDateTime());
    }
}
