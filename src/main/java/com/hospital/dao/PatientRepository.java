package com.hospital.dao;

import com.hospital.exception.DuplicateEntryException;
import com.hospital.model.Patient;
import java.util.List;

/**
 * Interface for Patient Data Access Object.
 * Follows Interface Segregation Principle - defines contract for Patient data operations.
 */
public interface PatientRepository {
    List<Patient> getAllPatients();
    Patient getPatientById(int id);
    boolean addPatient(Patient patient) throws DuplicateEntryException;
    boolean updatePatient(Patient patient);
    boolean deletePatient(int id);
    List<Patient> searchPatients(String query);
}
