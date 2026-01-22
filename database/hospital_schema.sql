-- =======================================================================================
-- Hospital Management System Database Schema
-- Based on the provided ER Diagram
-- =======================================================================================

DROP DATABASE IF EXISTS hospital_db;
CREATE DATABASE hospital_db;
USE hospital_db;

-- =======================================================================================
-- 1. Table Definitions
-- =======================================================================================

-- Department Table
CREATE TABLE Department (
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(100)
);

-- Doctor Table
CREATE TABLE Doctor (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100),
    department_id INT,
    FOREIGN KEY (department_id) REFERENCES Department(department_id)
);

-- Patient Table
CREATE TABLE Patient (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    contact VARCHAR(50)
);

-- MedicalInventory Table
CREATE TABLE MedicalInventory (
    med_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    quantity INT DEFAULT 0,
    expiry_date DATE
);

-- Appointment Table
CREATE TABLE Appointment (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    status VARCHAR(50) DEFAULT 'Scheduled',
    appointment_date DATETIME NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES Patient(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id)
);

-- Prescription Table
CREATE TABLE Prescription (
    prescription_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    prescription_date DATE NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES Patient(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id)
);

-- PrescriptionItem Table
CREATE TABLE PrescriptionItem (
    prescription_item_id INT AUTO_INCREMENT PRIMARY KEY,
    prescription_id INT NOT NULL,
    med_id INT NOT NULL,
    dosage VARCHAR(255),
    FOREIGN KEY (prescription_id) REFERENCES Prescription(prescription_id),
    FOREIGN KEY (med_id) REFERENCES MedicalInventory(med_id)
);

-- PatientFeedback Table
-- Linked to Patient and Doctor as per ERD relationships
CREATE TABLE PatientFeedback (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comments VARCHAR(255),
    FOREIGN KEY (patient_id) REFERENCES Patient(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_id)
);

-- =======================================================================================
-- 2. Sample Data Insertion (Adapted for new schema)
-- =======================================================================================

INSERT INTO Department (name, location) VALUES 
('Cardiology', 'Building A, Floor 3'),
('Neurology', 'Building A, Floor 2'),
('Pediatrics', 'Building B, Floor 1'),
('Orthopedics', 'Building C, Floor 1');

INSERT INTO Doctor (name, specialization, department_id) VALUES 
('John Smith', 'Cardiologist', 1),
('Emily Davis', 'Neurologist', 2),
('Sarah Wilson', 'Pediatrician', 3),
('Michael Brown', 'Orthopedic Surgeon', 4);

INSERT INTO Patient (name, date_of_birth, contact) VALUES 
('Alice Johnson', '1985-04-12', '555-1001'),
('Bob Williams', '1990-08-23', '555-1002'),
('Charlie Miller', '1978-11-30', '555-1003');

INSERT INTO MedicalInventory (name, quantity, expiry_date) VALUES 
('Aspirin 100mg', 500, '2025-12-31'),
('Amoxicillin 500mg', 200, '2024-06-30'),
('Ibuprofen 200mg', 400, '2025-10-15');

INSERT INTO Appointment (patient_id, doctor_id, status, appointment_date) VALUES 
(1, 1, 'Completed', '2023-11-01 09:00:00'),
(2, 2, 'Scheduled', '2023-11-02 10:00:00');

INSERT INTO Prescription (patient_id, doctor_id, prescription_date) VALUES 
(1, 1, '2023-11-01');

INSERT INTO PrescriptionItem (prescription_id, med_id, dosage) VALUES 
(1, 1, '1 tablet daily');

INSERT INTO PatientFeedback (patient_id, doctor_id, rating, comments) VALUES 
(1, 1, 5, 'Dr. Smith was very professional.');
