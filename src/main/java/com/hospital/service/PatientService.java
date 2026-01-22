package com.hospital.service;

import com.hospital.exception.DuplicateEntryException;
import com.hospital.model.Patient;
import java.util.List;

/**
 * Interface for Patient Service.
 * Follows Dependency Inversion Principle - high-level modules depend on abstractions.
 */
public interface PatientService {
    List<Patient> getAllPatients();
    boolean addPatient(Patient patient) throws DuplicateEntryException;
    boolean updatePatient(Patient patient);
    boolean deletePatient(int id);
    List<Patient> searchPatients(String query);
    void sortPatientsByName();
}
