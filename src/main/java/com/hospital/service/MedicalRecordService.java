package com.hospital.service;

import com.hospital.dao.MedicalRecordDAO;
import org.bson.Document;

import java.util.List;

/**
 * Service interface for Medical Record operations.
 * Follows Dependency Inversion Principle.
 */
public interface MedicalRecordService {
    boolean addMedicalRecord(int patientId, int doctorId, String note, String diagnosis, Integer appointmentId);
    List<Document> getMedicalRecordsByPatient(int patientId);
    List<Document> getRecordCountsLast30Days();
}
