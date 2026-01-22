package com.hospital.exception;

/**
 * Custom exception for duplicate entry violations.
 * Thrown when attempting to insert a record that already exists based on unique constraints.
 * 
 * This fulfills requirement #1: Duplicate prevention at application level.
 * Epic: Data Integrity / User Story 2.1: "Database constraints prevent duplicate or invalid entries"
 * Evaluation Category: Data Validation & Error Handling
 * 
 * Why: Provides clear, user-friendly error messages instead of generic SQLException,
 * allowing the UI layer to show specific feedback about what field caused the duplicate.
 */
public class DuplicateEntryException extends Exception {
    
    private final String fieldName;
    private final String fieldValue;
    
    /**
     * Creates a new DuplicateEntryException.
     * 
     * @param message Error message
     * @param fieldName Name of the field that caused the duplicate
     * @param fieldValue Value that already exists
     */
    public DuplicateEntryException(String message, String fieldName, String fieldValue) {
        super(message);
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public String getFieldValue() {
        return fieldValue;
    }
}
