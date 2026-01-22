package com.hospital.service;

import com.hospital.dao.AppointmentDAO;
import com.hospital.model.Appointment;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service layer for managing Appointment business logic.
 * Implements Caching, Conflict Detection, and CRUD operations.
 * Follows SOLID principles: Single Responsibility, Dependency Inversion.
 * 
 * This service implements requirement #5: Appointment management with duplicate prevention.
 */
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentDAO appointmentDAO;
    // In-memory cache: Maps ID to Appointment object for O(1) retrieval
    private static Map<Integer, Appointment> appointmentCache = new HashMap<>();
    private static List<Appointment> appointmentListCache = new ArrayList<>();
    private static boolean isCacheDirty = true; // Flag to indicate if we need to re-fetch from DB

    public AppointmentServiceImpl() {
        this.appointmentDAO = new AppointmentDAO();
    }

    /**
     * Retrieves all appointments, utilizing caching.
     * 
     * @return List of Appointments
     */
    public List<Appointment> getAllAppointments() {
        if (isCacheDirty) {
            refreshCache();
        }
        return new ArrayList<>(appointmentListCache);
    }

    /**
     * Refreshes the in-memory cache from the database.
     */
    private void refreshCache() {
        System.out.println("DEBUG: Appointment cache invalid or empty. Fetching from Database...");
        long start = System.nanoTime();

        List<Appointment> appointments = appointmentDAO.getAllAppointments();

        // Clear and rebuild caches
        appointmentCache.clear();
        appointmentListCache.clear();
        for (Appointment a : appointments) {
            appointmentCache.put(a.getAppointmentId(), a);
            appointmentListCache.add(a);
        }

        isCacheDirty = false;
        long end = System.nanoTime();
        System.out.println("DEBUG: Appointment cache refreshed in " + (end - start) / 1_000_000.0 + " ms.");
    }

    /**
     * Adds an appointment and invalidates cache.
     * Does NOT check for conflicts here - that should be done in the controller
     * before calling this method.
     * 
     * @param appointment Appointment to add
     * @return true if successful
     */
    public boolean addAppointment(Appointment appointment) {
        boolean success = appointmentDAO.addAppointment(appointment);
        if (success) {
            isCacheDirty = true; // Invalidate cache on write
        }
        return success;
    }

    /**
     * Updates an appointment and invalidates cache.
     * Does NOT check for conflicts here - that should be done in the controller
     * before calling this method.
     * 
     * @param appointment Appointment to update
     * @return true if successful
     */
    public boolean updateAppointment(Appointment appointment) {
        boolean success = appointmentDAO.updateAppointment(appointment);
        if (success) {
            isCacheDirty = true;
        }
        return success;
    }

    /**
     * Deletes an appointment and invalidates cache.
     * 
     * @param id Appointment ID to delete
     * @return true if successful
     */
    public boolean deleteAppointment(int id) {
        boolean success = appointmentDAO.deleteAppointment(id);
        if (success) {
            isCacheDirty = true;
        }
        return success;
    }

    /**
     * Searches appointments.
     * Uses In-Memory search if cache is valid for speed.
     */
    public List<Appointment> searchAppointments(String query) {
        if (isCacheDirty) {
            refreshCache();
        }

        String lowerQuery = query.toLowerCase();

        // In-memory linear search (O(n))
        return appointmentListCache.stream()
                .filter(a -> String.valueOf(a.getAppointmentId()).contains(query) ||
                        a.getStatus().toLowerCase().contains(lowerQuery))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Checks for duplicate/conflicting appointments.
     * This method is critical for requirement #5: duplicate prevention.
     * 
     * A conflict exists if the same doctor has an appointment at the same date/time,
     * excluding the appointment being updated (if any).
     * 
     * @param doctorId Doctor ID to check
     * @param appointmentDate Date and time of the appointment
     * @param excludeAppointmentId Appointment ID to exclude (for updates) or -1 for new appointments
     * @return true if a conflict exists, false otherwise
     */
    public boolean hasConflict(int doctorId, LocalDateTime appointmentDate, int excludeAppointmentId) {
        return appointmentDAO.hasConflict(doctorId, appointmentDate, excludeAppointmentId);
    }
}
