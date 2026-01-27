package com.hospital.dao;

import com.hospital.model.Doctor;
import com.hospital.util.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Doctor entities.
 * Handles SQL operations including CRUD and Search.
 * Implements IDoctorDAO interface following SOLID principles.
 */
public class DoctorDAO implements DoctorRepository {

    /**
     * Retrieves all doctors from the database.
     * 
     * @return List of Doctor objects
     */
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM Doctor ORDER BY name";

        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    doctors.add(mapResultSetToDoctor(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseHelper.releaseConnection(conn);
            }
        }
        return doctors;
    }

    /**
     * Retrieves a doctor by ID.
     * 
     * @param id Doctor ID
     * @return Doctor object or null if not found
     */
    public Doctor getDoctorById(int id) {
        String sql = "SELECT * FROM Doctor WHERE doctor_id = ?";

        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    return mapResultSetToDoctor(rs);
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
     * Adds a new doctor to the database.
     * 
     * @param doctor Doctor object to add
     * @return true if successful
     */
    public boolean addDoctor(Doctor doctor) {
        String sql = "INSERT INTO Doctor (name, specialization, department_id) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, doctor.getName());
                pstmt.setString(2, doctor.getSpecialization());
                pstmt.setInt(3, doctor.getDepartmentId());

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
     * Updates an existing doctor's details.
     * 
     * @param doctor Doctor object with updated details
     * @return true if successful
     */
    public boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE Doctor SET name=?, specialization=?, department_id=? WHERE doctor_id=?";

        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, doctor.getName());
                pstmt.setString(2, doctor.getSpecialization());
                pstmt.setInt(3, doctor.getDepartmentId());
                pstmt.setInt(4, doctor.getDoctorId());

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
     * Deletes a doctor by ID.
     * 
     * @param id Doctor ID
     * @return true if successful
     */
    public boolean deleteDoctor(int id) {
        String sql = "DELETE FROM Doctor WHERE doctor_id=?";

        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
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
     * Searches for doctors by Name or Specialization.
     * 
     * @param query Search string
     * @return List of matching doctors
     */
    public List<Doctor> searchDoctors(String query) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM Doctor WHERE doctor_id LIKE ? OR name LIKE ? OR specialization LIKE ?";

        String searchPattern = "%" + query + "%";

        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, searchPattern);
                pstmt.setString(2, searchPattern);
                pstmt.setString(3, searchPattern);

                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    doctors.add(mapResultSetToDoctor(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseHelper.releaseConnection(conn);
            }
        }
        return doctors;
    }

    /**
     * Gets doctors by department ID.
     * 
     * @param departmentId Department ID
     * @return List of doctors in the department
     */
    public List<Doctor> getDoctorsByDepartment(int departmentId) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM Doctor WHERE department_id = ? ORDER BY name";

        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, departmentId);
                ResultSet rs = pstmt.executeQuery();

                while (rs.next()) {
                    doctors.add(mapResultSetToDoctor(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseHelper.releaseConnection(conn);
            }
        }
        return doctors;
    }

    private Doctor mapResultSetToDoctor(ResultSet rs) throws SQLException {
        return new Doctor(
                rs.getInt("doctor_id"),
                rs.getString("name"),
                rs.getString("specialization"),
                rs.getInt("department_id"));
    }

    /**
     * Retrieves all doctors with their department names for UI display.
     * Uses JOIN to get department information in a single query.
     * 
     * @return List of DoctorDTO objects with department names
     */
    public List<com.hospital.model.DoctorDTO> getAllDoctorsWithDepartment() {
        List<com.hospital.model.DoctorDTO> doctors = new ArrayList<>();
        String sql = "SELECT d.doctor_id, d.name, d.specialization, d.department_id, dep.name AS department_name " +
                "FROM Doctor d " +
                "LEFT JOIN Department dep ON d.department_id = dep.department_id " +
                "ORDER BY d.name";

        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    com.hospital.model.DoctorDTO doctorDTO = new com.hospital.model.DoctorDTO(
                            rs.getInt("doctor_id"),
                            rs.getString("name"),
                            rs.getString("specialization"),
                            rs.getString("department_name"),
                            rs.getInt("department_id"));
                    doctors.add(doctorDTO);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseHelper.releaseConnection(conn);
            }
        }
        return doctors;
    }

    /**
     * Searches for doctors with department names by Name or Specialization.
     * 
     * @param query Search string
     * @return List of matching DoctorDTO objects with department names
     */
    public List<com.hospital.model.DoctorDTO> searchDoctorsWithDepartment(String query) {
        List<com.hospital.model.DoctorDTO> doctors = new ArrayList<>();
        String sql = "SELECT d.doctor_id, d.name, d.specialization, d.department_id, dep.name AS department_name " +
                "FROM Doctor d " +
                "LEFT JOIN Department dep ON d.department_id = dep.department_id " +
                "WHERE d.doctor_id LIKE ? OR d.name LIKE ? OR d.specialization LIKE ? OR dep.name LIKE ?";

        String searchPattern = "%" + query + "%";

        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, searchPattern);
                pstmt.setString(2, searchPattern);
                pstmt.setString(3, searchPattern);
                pstmt.setString(4, searchPattern);

                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    com.hospital.model.DoctorDTO doctorDTO = new com.hospital.model.DoctorDTO(
                            rs.getInt("doctor_id"),
                            rs.getString("name"),
                            rs.getString("specialization"),
                            rs.getString("department_name"),
                            rs.getInt("department_id"));
                    doctors.add(doctorDTO);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseHelper.releaseConnection(conn);
            }
        }
        return doctors;
    }
}
