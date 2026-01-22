package com.hospital.service;

import com.hospital.dao.MedicalRecordDAO;
import org.bson.Document;

import java.util.List;

/**
 * Service implementation for Medical Record operations.
 * Delegates to MedicalRecordDAO for MongoDB operations.
 * 
 * This fulfills requirement #2: NoSQL service layer for medical records.
 * Epic: Medical Records Management / Evaluation Category: Service Layer Architecture
 */
public class MedicalRecordServiceImpl implements MedicalRecordService {
    
    private final MedicalRecordDAO medicalRecordDAO;
    
    public MedicalRecordServiceImpl() {
        this.medicalRecordDAO = new MedicalRecordDAO();
    }
    
    @Override
    public boolean addMedicalRecord(int patientId, int doctorId, String note, String diagnosis, Integer appointmentId) {
        return medicalRecordDAO.insertMedicalRecord(patientId, doctorId, note, diagnosis, appointmentId);
    }
    
    @Override
    public List<Document> getMedicalRecordsByPatient(int patientId) {
        return medicalRecordDAO.getMedicalRecordsByPatient(patientId);
    }
    
    @Override
    public List<Document> getRecordCountsLast30Days() {
        return medicalRecordDAO.getRecordCountsLast30Days();
    }
}
