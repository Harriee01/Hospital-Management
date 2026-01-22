package com.hospital.dao;

import com.hospital.model.Doctor;
import java.util.List;

/**
 * Interface for Doctor Data Access Object.
 * Follows Interface Segregation Principle - defines contract for Doctor data operations.
 */
public interface DoctorRepository {
    List<Doctor> getAllDoctors();
    Doctor getDoctorById(int id);
    boolean addDoctor(Doctor doctor);
    boolean updateDoctor(Doctor doctor);
    boolean deleteDoctor(int id);
    List<Doctor> searchDoctors(String query);
    List<Doctor> getDoctorsByDepartment(int departmentId);
}
