package com.hospital.model;

/**
 * Model class representing a Doctor entity.
 */
public class Doctor {
    private int doctorId;
    private String name;
    private String specialization;
    private int departmentId;

    public Doctor(int doctorId, String name, String specialization, int departmentId) {
        this.doctorId = doctorId;
        this.name = name;
        this.specialization = specialization;
        this.departmentId = departmentId;
    }

    public Doctor(String name, String specialization, int departmentId) {
        this(0, name, specialization, departmentId);
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public String toString() {
        return name + " (" + specialization + ")";
    }
}
