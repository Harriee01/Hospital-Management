package com.hospital.model;

import java.time.LocalDate;

/**
 * Model class representing a Patient entity.
 * 
 * A Patient represents a person registered in the hospital system.
 * Contains personal information: name, date of birth, and contact number.
 * Patients are linked to Appointments, Prescriptions, and PatientFeedback
 * through foreign key relationships in the database.
 */
public class Patient {
    private int patientId;
    private String name;
    private LocalDate dateOfBirth;
    private String contact;

    public Patient(int patientId, String name, LocalDate dateOfBirth, String contact) {
        this.patientId = patientId;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.contact = contact;
    }

    public Patient(String name, LocalDate dateOfBirth, String contact) {
        this(0, name, dateOfBirth, contact);
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return name;
    }
}
