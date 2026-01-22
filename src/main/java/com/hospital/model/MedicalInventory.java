package com.hospital.model;

import java.time.LocalDate;

public class MedicalInventory {
    private int medId;
    private String name;
    private int quantity;
    private LocalDate expiryDate;

    public MedicalInventory(int medId, String name, int quantity, LocalDate expiryDate) {
        this.medId = medId;
        this.name = name;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
    }

    public MedicalInventory(String name, int quantity, LocalDate expiryDate) {
        this(0, name, quantity, expiryDate);
    }

    public int getMedId() {
        return medId;
    }

    public void setMedId(int medId) {
        this.medId = medId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return name;
    }
}
