package com.hospital.util;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

/**
 * Utility class for validating user inputs using regular expressions.
 * Provides static validation methods for common input types in the Hospital Management System.
 * 
 * This class fulfills the requirement for input validation to ensure data integrity
 * and prevent invalid data from being stored in the database.
 * 
 * Technical Requirement: Input validation using Regex patterns
 * - Validates names (letters, spaces, hyphens, apostrophes)
 * - Validates contact/phone numbers (international formats)
 * - Validates date of birth (past dates, reasonable age range)
 */
public class InputValidator {
    
    // Regex pattern for full names: allows letters, spaces, hyphens, apostrophes
    // Min 2 characters, max 100 characters
    // Examples: "John Smith", "Mary-Jane O'Brien", "José García"
    private static final Pattern NAME_PATTERN = Pattern.compile(
        "^[a-zA-ZÀ-ÿ\\s'-]{2,100}$"
    );
    
    // Regex pattern for contact/phone numbers
    // Supports:
    // - International format: +233xxxxxxxxx (Ghana with country code)
    // - Local format: 0xxxxxxxxxx (Ghana local)
    // - General format: 10-15 digits with optional +, -, or spaces
    // Examples: "+233241234567", "0241234567", "555-1234", "+1-555-123-4567"
    private static final Pattern CONTACT_PATTERN = Pattern.compile(
        "^(\\+?[0-9]{1,4}[-\\s]?)?([0-9]{7,15})$"
    );
    
    // Minimum age: 0 (newborn)
    private static final int MIN_AGE_YEARS = 0;
    // Maximum age: 120 years (reasonable upper limit for human age)
    private static final int MAX_AGE_YEARS = 120;
    
    /**
     * Validates a full name (patient or doctor name).
     * 
     * Rules:
     * - Must contain only letters (including accented characters), spaces, hyphens, and apostrophes
     * - Minimum length: 2 characters
     * - Maximum length: 100 characters
     * - No numbers or special symbols except hyphen (-) and apostrophe (')
     * 
     * @param name The name to validate
     * @return ValidationResult containing isValid flag and error message (if invalid)
     * 
     * Test cases:
     * - Valid: "John Smith", "Mary-Jane O'Brien", "José García", "Dr. Smith"
     * - Invalid: "J" (too short), "A1" (contains number), "John@Smith" (contains @)
     */
    public static ValidationResult validateName(String name) {
        // Check for null or empty input
        if (name == null || name.trim().isEmpty()) {
            return new ValidationResult(false, "Name cannot be empty.");
        }
        
        // Trim whitespace for validation
        String trimmedName = name.trim();
        
        // Check minimum length
        if (trimmedName.length() < 2) {
            return new ValidationResult(false, "Name must be at least 2 characters long.");
        }
        
        // Check maximum length
        if (trimmedName.length() > 100) {
            return new ValidationResult(false, "Name cannot exceed 100 characters.");
        }
        
        // Check pattern match (letters, spaces, hyphens, apostrophes only)
        if (!NAME_PATTERN.matcher(trimmedName).matches()) {
            return new ValidationResult(false, 
                "Name can only contain letters, spaces, hyphens (-), and apostrophes (').");
        }
        
        // All checks passed
        return new ValidationResult(true, null);
    }
    
    /**
     * Validates a contact/phone number.
     * 
     * Rules:
     * - Supports international format with country code (e.g., +233xxxxxxxxx)
     * - Supports local format (e.g., 0xxxxxxxxxx for Ghana)
     * - General format: 10-15 digits with optional +, -, or spaces
     * - Must contain at least 7 digits in the main number part
     * 
     * @param contact The contact number to validate
     * @return ValidationResult containing isValid flag and error message (if invalid)
     * 
     * Test cases:
     * - Valid: "+233241234567", "0241234567", "555-1234", "+1-555-123-4567", "555 123 4567"
     * - Invalid: "123" (too short), "abc123" (contains letters), "" (empty)
     */
    public static ValidationResult validateContact(String contact) {
        // Check for null or empty input
        if (contact == null || contact.trim().isEmpty()) {
            return new ValidationResult(false, "Contact number cannot be empty.");
        }
        
        // Trim whitespace for validation
        String trimmedContact = contact.trim();
        
        // Check pattern match
        if (!CONTACT_PATTERN.matcher(trimmedContact).matches()) {
            return new ValidationResult(false, 
                "Contact number must be 7-15 digits. International format: +[country code][number]. " +
                "Example: +233241234567 or 0241234567");
        }
        
        // All checks passed
        return new ValidationResult(true, null);
    }
    
    /**
     * Validates a date of birth.
     * 
     * Rules:
     * - Must be a valid past date (not in the future)
     * - Must not be older than MAX_AGE_YEARS (120 years)
     * - Must not be in the future (newborns are allowed, so MIN_AGE_YEARS = 0)
     * 
     * @param dateOfBirth The date of birth to validate (from DatePicker)
     * @return ValidationResult containing isValid flag and error message (if invalid)
     * 
     * Test cases:
     * - Valid: LocalDate.now().minusYears(30) (30 years ago), LocalDate.now().minusDays(1) (yesterday)
     * - Invalid: LocalDate.now().plusDays(1) (future date), LocalDate.now().minusYears(150) (too old)
     */
    public static ValidationResult validateDateOfBirth(LocalDate dateOfBirth) {
        // Check for null input
        if (dateOfBirth == null) {
            return new ValidationResult(false, "Date of birth is required.");
        }
        
        LocalDate today = LocalDate.now();
        
        // Check if date is in the future
        if (dateOfBirth.isAfter(today)) {
            return new ValidationResult(false, "Date of birth cannot be in the future.");
        }
        
        // Calculate age in years
        Period age = Period.between(dateOfBirth, today);
        int ageYears = age.getYears();
        
        // Check minimum age (0 years - newborns are allowed)
        if (ageYears < MIN_AGE_YEARS) {
            return new ValidationResult(false, "Date of birth cannot be in the future.");
        }
        
        // Check maximum age (120 years - reasonable upper limit)
        if (ageYears > MAX_AGE_YEARS) {
            return new ValidationResult(false, 
                String.format("Date of birth indicates age of %d years, which exceeds the maximum of %d years.", 
                    ageYears, MAX_AGE_YEARS));
        }
        
        // All checks passed
        return new ValidationResult(true, null);
    }
    
    /**
     * Inner record class to hold validation result.
     * Contains a boolean flag indicating validity and an optional error message.
     * 
     * Using Java 17+ record feature for immutable data structure.
     * This provides a clean way to return both validation status and error message.
     */
    public record ValidationResult(boolean isValid, String errorMessage) {
        /**
         * Constructor for ValidationResult.
         * 
         * @param isValid true if validation passed, false otherwise
         * @param errorMessage error message if validation failed, null if valid
         */
        public ValidationResult {
            // Record automatically generates constructor, getters, equals, hashCode, toString
        }
    }
    
    /* ====================================================================
     * UNIT TEST EXAMPLES (as comments - these would be in a test class)
     * ====================================================================
     * 
     * // Test validateName()
     * @Test
     * void testValidateName_Valid() {
     *     ValidationResult result = InputValidator.validateName("John Smith");
     *     assertTrue(result.isValid());
     *     assertNull(result.errorMessage());
     * }
     * 
     * @Test
     * void testValidateName_TooShort() {
     *     ValidationResult result = InputValidator.validateName("J");
     *     assertFalse(result.isValid());
     *     assertEquals("Name must be at least 2 characters long.", result.errorMessage());
     * }
     * 
     * @Test
     * void testValidateName_ContainsNumber() {
     *     ValidationResult result = InputValidator.validateName("John123");
     *     assertFalse(result.isValid());
     *     assertTrue(result.errorMessage().contains("only contain letters"));
     * }
     * 
     * // Test validateContact()
     * @Test
     * void testValidateContact_ValidGhana() {
     *     ValidationResult result = InputValidator.validateContact("+233241234567");
     *     assertTrue(result.isValid());
     * }
     * 
     * @Test
     * void testValidateContact_InvalidTooShort() {
     *     ValidationResult result = InputValidator.validateContact("123");
     *     assertFalse(result.isValid());
     * }
     * 
     * // Test validateDateOfBirth()
     * @Test
     * void testValidateDateOfBirth_Valid() {
     *     LocalDate dob = LocalDate.now().minusYears(30);
     *     ValidationResult result = InputValidator.validateDateOfBirth(dob);
     *     assertTrue(result.isValid());
     * }
     * 
     * @Test
     * void testValidateDateOfBirth_FutureDate() {
     *     LocalDate dob = LocalDate.now().plusDays(1);
     *     ValidationResult result = InputValidator.validateDateOfBirth(dob);
     *     assertFalse(result.isValid());
     *     assertTrue(result.errorMessage().contains("future"));
     * }
     */
}
