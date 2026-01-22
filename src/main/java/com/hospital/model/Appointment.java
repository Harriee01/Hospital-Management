package com.hospital.model;

import java.time.LocalDateTime;

/**
 * Model class representing an Appointment entity.
 * 
 * An Appointment links a Patient with a Doctor at a specific date and time.
 * Contains appointment status (Scheduled, Completed, Cancelled) and references
 * to both patient and doctor entities from the relational database.
 */
public class Appointment {
    private int appointmentId;
    private int patientId;
    private int doctorId;
    private String status;
    private LocalDateTime appointmentDate;

    public Appointment(int appointmentId, int patientId, int doctorId, String status, LocalDateTime appointmentDate) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.status = status;
        this.appointmentDate = appointmentDate;
    }

    public Appointment(int patientId, int doctorId, String status, LocalDateTime appointmentDate) {
        this(0, patientId, doctorId, status, appointmentDate);
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
}
