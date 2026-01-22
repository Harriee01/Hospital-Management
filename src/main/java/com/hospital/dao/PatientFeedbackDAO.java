package com.hospital.dao;

import com.hospital.model.PatientFeedback;
import com.hospital.util.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for PatientFeedback entities.
 * Handles SQL operations including CRUD and Search.
 */
public class PatientFeedbackDAO {

    /**
     * Retrieves all patient feedback from the database.
     * 
     * @return List of PatientFeedback objects
     */
    public List<PatientFeedback> getAllFeedback() {
        List<PatientFeedback> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM PatientFeedback ORDER BY feedback_id DESC";

        try (Connection conn = DatabaseHelper.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                feedbackList.add(mapResultSetToFeedback(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbackList;
    }

    /**
     * Retrieves a feedback by ID.
     * 
     * @param id Feedback ID
     * @return PatientFeedback object or null if not found
     */
    public PatientFeedback getFeedbackById(int id) {
        String sql = "SELECT * FROM PatientFeedback WHERE feedback_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToFeedback(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds a new feedback to the database.
     * 
     * @param feedback PatientFeedback object to add
     * @return true if successful
     */
    public boolean addFeedback(PatientFeedback feedback) {
        String sql = "INSERT INTO PatientFeedback (patient_id, doctor_id, rating, comments) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, feedback.getPatientId());
            pstmt.setInt(2, feedback.getDoctorId());
            pstmt.setInt(3, feedback.getRating());
            pstmt.setString(4, feedback.getComments());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing feedback's details.
     * 
     * @param feedback PatientFeedback object with updated details
     * @return true if successful
     */
    public boolean updateFeedback(PatientFeedback feedback) {
        String sql = "UPDATE PatientFeedback SET patient_id=?, doctor_id=?, rating=?, comments=? WHERE feedback_id=?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, feedback.getPatientId());
            pstmt.setInt(2, feedback.getDoctorId());
            pstmt.setInt(3, feedback.getRating());
            pstmt.setString(4, feedback.getComments());
            pstmt.setInt(5, feedback.getFeedbackId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a feedback by ID.
     * 
     * @param id Feedback ID
     * @return true if successful
     */
    public boolean deleteFeedback(int id) {
        String sql = "DELETE FROM PatientFeedback WHERE feedback_id=?";

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
     * Gets feedback by patient ID.
     * 
     * @param patientId Patient ID
     * @return List of feedback from the patient
     */
    public List<PatientFeedback> getFeedbackByPatient(int patientId) {
        List<PatientFeedback> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM PatientFeedback WHERE patient_id = ? ORDER BY feedback_id DESC";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                feedbackList.add(mapResultSetToFeedback(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbackList;
    }

    /**
     * Gets feedback by doctor ID.
     * 
     * @param doctorId Doctor ID
     * @return List of feedback for the doctor
     */
    public List<PatientFeedback> getFeedbackByDoctor(int doctorId) {
        List<PatientFeedback> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM PatientFeedback WHERE doctor_id = ? ORDER BY feedback_id DESC";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                feedbackList.add(mapResultSetToFeedback(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbackList;
    }

    /**
     * Gets feedback by rating.
     * 
     * @param rating Rating value (1-5)
     * @return List of feedback with the given rating
     */
    public List<PatientFeedback> getFeedbackByRating(int rating) {
        List<PatientFeedback> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM PatientFeedback WHERE rating = ? ORDER BY feedback_id DESC";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, rating);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                feedbackList.add(mapResultSetToFeedback(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbackList;
    }

    /**
     * Gets average rating for a doctor.
     * 
     * @param doctorId Doctor ID
     * @return Average rating or 0.0 if no feedback
     */
    public double getAverageRatingForDoctor(int doctorId) {
        String sql = "SELECT AVG(rating) as avg_rating FROM PatientFeedback WHERE doctor_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("avg_rating");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Searches for feedback by comments.
     * 
     * @param query Search string
     * @return List of matching feedback
     */
    public List<PatientFeedback> searchFeedback(String query) {
        List<PatientFeedback> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM PatientFeedback WHERE comments LIKE ?";

        String searchPattern = "%" + query + "%";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                feedbackList.add(mapResultSetToFeedback(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbackList;
    }

    private PatientFeedback mapResultSetToFeedback(ResultSet rs) throws SQLException {
        return new PatientFeedback(
                rs.getInt("feedback_id"),
                rs.getInt("patient_id"),
                rs.getInt("doctor_id"),
                rs.getInt("rating"),
                rs.getString("comments"));
    }
}
