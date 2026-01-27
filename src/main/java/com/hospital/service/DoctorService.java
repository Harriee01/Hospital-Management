package com.hospital.service;

import com.hospital.model.Doctor;
import java.util.List;

/**
 * Interface for Doctor Service.
 * Follows Dependency Inversion Principle - high-level modules depend on
 * abstractions.
 */
public interface DoctorService {
    List<Doctor> getAllDoctors();

    boolean addDoctor(Doctor doctor);

    boolean updateDoctor(Doctor doctor);

    boolean deleteDoctor(int id);

    List<Doctor> searchDoctors(String query);

    List<Doctor> getDoctorsByDepartment(int departmentId);

    List<com.hospital.model.DoctorDTO> getDoctorsWithDepartment();

    List<com.hospital.model.DoctorDTO> searchDoctorsWithDepartment(String query);
}
