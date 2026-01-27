package com.hospital.model;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Appointment with related patient and doctor
 * information.
 * Used in UI to display appointments with patient and doctor names for better
 * readability.
 * Fields: ID, patient_ID, patient name, doctor ID, doctor name, date, and
 * status
 */
public class AppointmentDTO {
    private int appointmentId;
    private int patientId;
    private String patientName;
    private int doctorId;
    private String doctorName;
    private LocalDateTime appointmentDate;
    private String status;

    public AppointmentDTO(int appointmentId, int patientId, String patientName,
            int doctorId, String doctorName, LocalDateTime appointmentDate, String status) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.appointmentDate = appointmentDate;
        this.status = status;
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

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Appointment #" + appointmentId + ": " + patientName + " with Dr. " + doctorName;
    }
}
