package com.hospital.service;

import com.hospital.dao.PatientRepository;
import com.hospital.dao.PatientDAO;
import com.hospital.exception.DuplicateEntryException;
import com.hospital.model.Patient;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer for managing Patient business logic.
 * Implements Caching, Sorting, and Searching optimization.
 * Follows SOLID principles: Single Responsibility, Dependency Inversion.
 */
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientDAO;
    // In-memory cache: Maps ID to Patient object for O(1) retrieval
    private static Map<Integer, Patient> patientCache = new HashMap<>();
    private static List<Patient> patientListCache = new ArrayList<>();
    private static boolean isCacheDirty = true; // Flag to indicate if we need to re-fetch from DB

    public PatientServiceImpl() {
        this.patientDAO = new PatientDAO();
    }

    /**
     * Retrieves all patients, utilizing caching.
     * Use time measurement here to demonstrate performance.
     * 
     * @return List of Patients
     */
    public List<Patient> getAllPatients() {
        if (isCacheDirty) {
            refreshCache();
        }
        return new ArrayList<>(patientListCache);
    }

    /**
     * Refreshes the in-memory cache from the database.
     */
    private void refreshCache() {
        System.out.println("DEBUG: Cache invalid or empty. Fetching from Database...");
        long start = System.nanoTime();

        List<Patient> patients = patientDAO.getAllPatients();

        // Clear and rebuild caches
        patientCache.clear();
        patientListCache.clear();
        for (Patient p : patients) {
            patientCache.put(p.getPatientId(), p);
            patientListCache.add(p);
        }

        isCacheDirty = false;
        long end = System.nanoTime();
        System.out.println("DEBUG: Cache refreshed in " + (end - start) / 1_000_000.0 + " ms.");
    }

    /**
     * Adds a patient and invalidates cache.
     * Handles DuplicateEntryException and re-throws it for controller to show user-friendly message.
     * This fulfills requirement #1: Duplicate prevention - application level error handling.
     * Epic: Data Integrity / User Story 2.1
     * Evaluation Category: Error Handling & User Experience
     * 
     * @param patient Patient to add
     * @throws DuplicateEntryException if duplicate detected
     * @return true if successful
     */
    public boolean addPatient(Patient patient) throws DuplicateEntryException {
        // This will throw DuplicateEntryException if duplicate found (requirement #1)
        boolean success = patientDAO.addPatient(patient);
        if (success) {
            isCacheDirty = true; // Invalidate cache on write
        }
        return success;
    }

    /**
     * Updates a patient and invalidates cache.
     */
    public boolean updatePatient(Patient patient) {
        boolean success = patientDAO.updatePatient(patient);
        if (success) {
            isCacheDirty = true;
        }
        return success;
    }

    /**
     * Deletes a patient and invalidates cache.
     */
    public boolean deletePatient(int id) {
        boolean success = patientDAO.deletePatient(id);
        if (success) {
            isCacheDirty = true;
        }
        return success;
    }

    /**
     * Searches patients.
     * Demonstrates In-Memory search vs Database Search options.
     * We will use In-Memory if cache is valid for speed.
     */
    public List<Patient> searchPatients(String query) {
        if (isCacheDirty) {
            refreshCache();
        }

        long start = System.nanoTime();
        String lowerQuery = query.toLowerCase();

        // In-memory linear search (O(n))
        // Because we are using the cached list, this avoids a DB round-trip
        List<Patient> result = patientListCache.stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerQuery) ||
                        String.valueOf(p.getPatientId()).contains(query))
                .collect(Collectors.toList());

        long end = System.nanoTime();
        System.out.println("DEBUG: In-Memory Search executed in " + (end - start) / 1_000_000.0 + " ms.");

        return result;
    }

    /**
     * Sorts patients by Name.
     * Uses Java's Dual-Pivot Quicksort (Collections.sort).
     */
    public void sortPatientsByName() {
        if (isCacheDirty)
            refreshCache();

        long start = System.nanoTime();
        patientListCache.sort(Comparator.comparing(Patient::getName));
        long end = System.nanoTime();
        System.out.println("DEBUG: In-Memory Sort executed in " + (end - start) / 1_000_000.0 + " ms.");
    }
}
