package com.hospital.model;

import java.time.LocalDate;

/**
 * Model class representing a Prescription entity.
 * 
 * A Prescription links a Patient with a Doctor and contains the date when the prescription
 * was issued. PrescriptionItems (medications) are associated with a Prescription through
 * the prescription_id foreign key, creating a one-to-many relationship.
 */
public class Prescription {
    private int prescriptionId;
    private int patientId;
    private int doctorId;
    private LocalDate prescriptionDate;

    public Prescription(int prescriptionId, int patientId, int doctorId, LocalDate prescriptionDate) {
        this.prescriptionId = prescriptionId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.prescriptionDate = prescriptionDate;
    }

    public Prescription(int patientId, int doctorId, LocalDate prescriptionDate) {
        this(0, patientId, doctorId, prescriptionDate);
    }

    public int getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(int prescriptionId) {
        this.prescriptionId = prescriptionId;
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

    public LocalDate getPrescriptionDate() {
        return prescriptionDate;
    }

    public void setPrescriptionDate(LocalDate prescriptionDate) {
        this.prescriptionDate = prescriptionDate;
    }
}
