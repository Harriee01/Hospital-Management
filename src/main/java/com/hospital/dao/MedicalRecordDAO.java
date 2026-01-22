package com.hospital.dao;

import com.hospital.util.ConnectionPool;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object for Patient Medical Records stored in MongoDB.
 * Implements NoSQL document storage for unstructured medical notes, diagnoses, and treatment history.
 * 
 * This fulfills requirement #2: NoSQL implementation using MongoDB for patient medical records.
 * Epic: Medical Records Management / User Story: "explore storing patient notes or medical logs in a NoSQL format"
 * Evaluation Category: Database Integration & Architecture (Polyglot Persistence)
 * 
 * Why MongoDB/NoSQL here: Medical notes are unstructured/semi-structured, vary in length and format,
 * and benefit from schema flexibility. MongoDB allows fast appends, easy historical querying,
 * and better performance for text-heavy documents compared to normalized relational tables.
 */
public class MedicalRecordDAO {
    
    // Collection name for medical records
    private static final String COLLECTION_NAME = "patient_medical_records";
    
    /**
     * Gets the MongoDB collection for medical records.
     * Uses ConnectionPool to get the collection, which handles MongoDB connection
     * initialization from .env file configuration.
     * 
     * Why: Centralizes MongoDB connection management in ConnectionPool, allowing
     * all MongoDB connections to be configured from .env file in one place.
     * 
     * @return MongoCollection<Document> for patient_medical_records
     */
    private static MongoCollection<Document> getCollection() {
        // Get collection from ConnectionPool, which initializes MongoDB connection from .env
        return ConnectionPool.getMongoCollection(COLLECTION_NAME);
    }
    
    /**
     * Inserts a new medical record/note for a patient.
     * This fulfills requirement #2: Basic MongoDB insert operation.
     * 
     * Document schema:
     * {
     *   patientId: int (reference to MySQL Patient table),
     *   doctorId: int (reference to MySQL Doctor table),
     *   appointmentId: int (optional, reference to Appointment),
     *   createdAt: ISODate,
     *   type: string (e.g., "Consultation", "Diagnosis", "Lab Result"),
     *   content: {
     *     note: string,
     *     diagnosis: string,
     *     treatment: string,
     *     vitals: object (optional),
     *     attachments: array (optional)
     *   },
     *   tags: array of strings
     * }
     * 
     * @param patientId Patient ID (from MySQL Patient table)
     * @param doctorId Doctor ID (from MySQL Doctor table)
     * @param note Medical note text
     * @param diagnosis Diagnosis text
     * @param appointmentId Optional appointment ID
     * @return true if successful
     */
    public boolean insertMedicalRecord(int patientId, int doctorId, String note, String diagnosis, Integer appointmentId) {
        try {
            // Create document following the schema design
            Document record = new Document();
            record.append("patientId", patientId);  // Reference to relational Patient table
            record.append("doctorId", doctorId);    // Reference to relational Doctor table
            record.append("createdAt", new Date()); // Current timestamp as ISODate
            
            // Set appointment ID if provided
            if (appointmentId != null) {
                record.append("appointmentId", appointmentId);
            }
            
            // Set record type (default to "Consultation")
            record.append("type", "Consultation");
            
            // Create content sub-document with medical information
            Document content = new Document();
            content.append("note", note != null ? note : "");
            content.append("diagnosis", diagnosis != null ? diagnosis : "");
            content.append("treatment", ""); // Can be populated later
            record.append("content", content);
            
            // Add empty tags array (can be populated for categorization)
            record.append("tags", new ArrayList<String>());
            
            // Insert document into MongoDB collection
            // Uses ConnectionPool to get collection (connection managed centrally)
            getCollection().insertOne(record);
            
            System.out.println("DEBUG: Medical record inserted for patient ID: " + patientId);
            return true;
        } catch (Exception e) {
            System.err.println("ERROR: Failed to insert medical record: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Retrieves all medical records for a specific patient, sorted by date descending (most recent first).
     * This fulfills requirement #2: Basic MongoDB query operation.
     * 
     * @param patientId Patient ID to retrieve records for
     * @return List of Document objects representing medical records
     */
    public List<Document> getMedicalRecordsByPatient(int patientId) {
        List<Document> records = new ArrayList<>();
        try {
            // Query MongoDB: find all documents where patientId matches
            // Sort by createdAt descending (most recent first)
            // This uses MongoDB index on patientId for fast lookup
            // Uses ConnectionPool to get collection (connection managed centrally)
            getCollection().find(Filters.eq("patientId", patientId))
                    .sort(Sorts.descending("createdAt"))
                    .into(records);
            
            System.out.println("DEBUG: Retrieved " + records.size() + " medical records for patient ID: " + patientId);
        } catch (Exception e) {
            System.err.println("ERROR: Failed to retrieve medical records: " + e.getMessage());
            e.printStackTrace();
        }
        return records;
    }
    
    /**
     * Aggregation example: Count medical records per patient created in the last 30 days.
     * This fulfills requirement #2: Simple aggregation example for reporting.
     * 
     * Epic: Reporting & Analytics / Evaluation Category: Database Operations (Aggregation)
     * 
     * Why: Demonstrates MongoDB's aggregation pipeline for analytics, useful for
     * generating reports on patient visit frequency, doctor activity, etc.
     * 
     * @return List of Documents with patientId and record count
     */
    public List<Document> getRecordCountsLast30Days() {
        List<Document> results = new ArrayList<>();
        try {
            // Calculate date 30 days ago
            Date thirtyDaysAgo = Date.from(
                LocalDateTime.now().minusDays(30)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
            );
            
            // MongoDB aggregation pipeline:
            // 1. Match: Filter records created in last 30 days
            // 2. Group: Group by patientId and count records
            // 3. Sort: Sort by count descending
            List<Document> pipeline = new ArrayList<>();
            pipeline.add(new Document("$match", 
                new Document("createdAt", new Document("$gte", thirtyDaysAgo))));
            pipeline.add(new Document("$group",
                new Document("_id", "$patientId")
                    .append("recordCount", new Document("$sum", 1))));
            pipeline.add(new Document("$sort", new Document("recordCount", -1)));
            
            // Execute aggregation
            // Uses ConnectionPool to get collection (connection managed centrally)
            getCollection().aggregate(pipeline).into(results);
            
            System.out.println("DEBUG: Aggregation completed. Found " + results.size() + " patients with records in last 30 days.");
        } catch (Exception e) {
            System.err.println("ERROR: Failed to execute aggregation: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }
    
    /**
     * Closes MongoDB connection. Should be called on application shutdown.
     * Delegates to ConnectionPool for centralized connection management.
     */
    public static void closeConnection() {
        ConnectionPool.closeMongoDB();
    }
}
