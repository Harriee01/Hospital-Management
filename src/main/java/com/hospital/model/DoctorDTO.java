package com.hospital.model;

/**
 * Data Transfer Object for Doctor with related department information.
 * Used in UI to display doctors with their department names.
 * Fields: ID, name, specialization, department name, department_ID
 */
public class DoctorDTO {
    private int doctorId;
    private String name;
    private String specialization;
    private String departmentName;
    private int departmentId;

    public DoctorDTO(int doctorId, String name, String specialization, String departmentName, int departmentId) {
        this.doctorId = doctorId;
        this.name = name;
        this.specialization = specialization;
        this.departmentName = departmentName;
        this.departmentId = departmentId;
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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public String toString() {
        return name + " (" + specialization + " - " + departmentName + ")";
    }
}
