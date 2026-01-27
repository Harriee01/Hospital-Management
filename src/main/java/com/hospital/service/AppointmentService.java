package com.hospital.service;

import com.hospital.model.Appointment;
import java.util.List;

/**
 * Interface for Appointment Service.
 * Follows Dependency Inversion Principle - high-level modules depend on
 * abstractions.
 * 
 * This interface defines the contract for appointment management operations,
 * including CRUD operations and conflict checking for requirement #5.
 */
public interface AppointmentService {
    List<Appointment> getAllAppointments();

    boolean addAppointment(Appointment appointment);

    boolean updateAppointment(Appointment appointment);

    boolean deleteAppointment(int id);

    List<Appointment> searchAppointments(String query);

    boolean hasConflict(int doctorId, java.time.LocalDateTime appointmentDate, int excludeAppointmentId);

    List<com.hospital.model.AppointmentDTO> getAllAppointmentsWithNames();

    List<com.hospital.model.AppointmentDTO> searchAppointmentsWithNames(String query);
}
