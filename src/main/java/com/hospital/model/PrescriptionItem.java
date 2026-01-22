package com.hospital.model;

/**
 * Model class representing a PrescriptionItem entity.
 * 
 * A PrescriptionItem represents a single medication item within a Prescription.
 * Links a Prescription to a MedicalInventory item (medication) and specifies
 * the dosage instructions for that medication.
 */
public class PrescriptionItem {
    private int prescriptionItemId;
    private int prescriptionId;
    private int medId;
    private String dosage;

    public PrescriptionItem(int prescriptionItemId, int prescriptionId, int medId, String dosage) {
        this.prescriptionItemId = prescriptionItemId;
        this.prescriptionId = prescriptionId;
        this.medId = medId;
        this.dosage = dosage;
    }

    public PrescriptionItem(int prescriptionId, int medId, String dosage) {
        this(0, prescriptionId, medId, dosage);
    }

    public int getPrescriptionItemId() {
        return prescriptionItemId;
    }

    public void setPrescriptionItemId(int prescriptionItemId) {
        this.prescriptionItemId = prescriptionItemId;
    }

    public int getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(int prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public int getMedId() {
        return medId;
    }

    public void setMedId(int medId) {
        this.medId = medId;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
}
