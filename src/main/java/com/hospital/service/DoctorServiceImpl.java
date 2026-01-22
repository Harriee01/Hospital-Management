package com.hospital.service;

import com.hospital.dao.DoctorRepository;
import com.hospital.dao.DoctorDAO;
import com.hospital.model.Doctor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer for managing Doctor business logic.
 * Implements Caching, Sorting, and Searching optimization.
 * Follows SOLID principles: Single Responsibility, Dependency Inversion.
 */
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorDAO;
    // In-memory cache: Maps ID to Doctor object for O(1) retrieval
    private static Map<Integer, Doctor> doctorCache = new HashMap<>();
    private static List<Doctor> doctorListCache = new ArrayList<>();
    private static boolean isCacheDirty = true; // Flag to indicate if we need to re-fetch from DB

    public DoctorServiceImpl() {
        this.doctorDAO = new DoctorDAO();
    }

    /**
     * Retrieves all doctors, utilizing caching.
     * 
     * @return List of Doctors
     */
    public List<Doctor> getAllDoctors() {
        if (isCacheDirty) {
            refreshCache();
        }
        return new ArrayList<>(doctorListCache);
    }

    /**
     * Refreshes the in-memory cache from the database.
     */
    private void refreshCache() {
        System.out.println("DEBUG: Doctor cache invalid or empty. Fetching from Database...");
        long start = System.nanoTime();

        List<Doctor> doctors = doctorDAO.getAllDoctors();

        // Clear and rebuild caches
        doctorCache.clear();
        doctorListCache.clear();
        for (Doctor d : doctors) {
            doctorCache.put(d.getDoctorId(), d);
            doctorListCache.add(d);
        }

        isCacheDirty = false;
        long end = System.nanoTime();
        System.out.println("DEBUG: Doctor cache refreshed in " + (end - start) / 1_000_000.0 + " ms.");
    }

    /**
     * Adds a doctor and invalidates cache.
     */
    public boolean addDoctor(Doctor doctor) {
        boolean success = doctorDAO.addDoctor(doctor);
        if (success) {
            isCacheDirty = true; // Invalidate cache on write
        }
        return success;
    }

    /**
     * Updates a doctor and invalidates cache.
     */
    public boolean updateDoctor(Doctor doctor) {
        boolean success = doctorDAO.updateDoctor(doctor);
        if (success) {
            isCacheDirty = true;
        }
        return success;
    }

    /**
     * Deletes a doctor and invalidates cache.
     */
    public boolean deleteDoctor(int id) {
        boolean success = doctorDAO.deleteDoctor(id);
        if (success) {
            isCacheDirty = true;
        }
        return success;
    }

    /**
     * Searches doctors.
     * Uses In-Memory search if cache is valid for speed.
     */
    public List<Doctor> searchDoctors(String query) {
        if (isCacheDirty) {
            refreshCache();
        }

        long start = System.nanoTime();
        String lowerQuery = query.toLowerCase();

        // In-memory linear search (O(n))
        // Because we are using the cached list, this avoids a DB round-trip
        List<Doctor> result = doctorListCache.stream()
                .filter(d -> d.getName().toLowerCase().contains(lowerQuery) ||
                        d.getSpecialization().toLowerCase().contains(lowerQuery) ||
                        String.valueOf(d.getDoctorId()).contains(query))
                .collect(Collectors.toList());

        long end = System.nanoTime();
        System.out.println("DEBUG: In-Memory Doctor Search executed in " + (end - start) / 1_000_000.0 + " ms.");

        return result;
    }

    /**
     * Gets doctors by department ID.
     * Uses cached data if available.
     */
    public List<Doctor> getDoctorsByDepartment(int departmentId) {
        if (isCacheDirty) {
            refreshCache();
        }

        // In-memory filter
        return doctorListCache.stream()
                .filter(d -> d.getDepartmentId() == departmentId)
                .collect(Collectors.toList());
    }
}
