package com.hospital.dao;

import com.hospital.model.Department;
import com.hospital.util.DatabaseHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Department entities.
 * Handles SQL operations including CRUD and Search.
 */
public class DepartmentDAO {

    /**
     * Retrieves all departments from the database.
     * 
     * @return List of Department objects
     */
    public List<Department> getAllDepartments() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM Department ORDER BY name";

        Connection conn = null;
        try {
            conn = DatabaseHelper.getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    departments.add(mapResultSetToDepartment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                DatabaseHelper.releaseConnection(conn);
            }
        }
        return departments;
    }

    /**
     * Retrieves a department by ID.
     * 
     * @param id Department ID
     * @return Department object or null if not found
     */
    public Department getDepartmentById(int id) {
        String sql = "SELECT * FROM Department WHERE department_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToDepartment(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds a new department to the database.
     * 
     * @param department Department object to add
     * @return true if successful
     */
    public boolean addDepartment(Department department) {
        String sql = "INSERT INTO Department (name, location) VALUES (?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, department.getName());
            pstmt.setString(2, department.getLocation());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing department's details.
     * 
     * @param department Department object with updated details
     * @return true if successful
     */
    public boolean updateDepartment(Department department) {
        String sql = "UPDATE Department SET name=?, location=? WHERE department_id=?";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, department.getName());
            pstmt.setString(2, department.getLocation());
            pstmt.setInt(3, department.getDepartmentId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a department by ID.
     * 
     * @param id Department ID
     * @return true if successful
     */
    public boolean deleteDepartment(int id) {
        String sql = "DELETE FROM Department WHERE department_id=?";

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
     * Searches for departments by Name or Location.
     * 
     * @param query Search string
     * @return List of matching departments
     */
    public List<Department> searchDepartments(String query) {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM Department WHERE department_id LIKE ? OR name LIKE ? OR location LIKE ?";

        String searchPattern = "%" + query + "%";

        try (Connection conn = DatabaseHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                departments.add(mapResultSetToDepartment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }

    private Department mapResultSetToDepartment(ResultSet rs) throws SQLException {
        return new Department(
                rs.getInt("department_id"),
                rs.getString("name"),
                rs.getString("location"));
    }
}
