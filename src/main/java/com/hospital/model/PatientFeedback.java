package com.hospital.model;

/**
 * Model class representing a PatientFeedback entity.
 * 
 * PatientFeedback stores patient ratings and comments about doctors.
 * Links a Patient to a Doctor with a rating (1-5 scale) and optional comments.
 * Used for quality assurance and doctor performance evaluation.
 */
public class PatientFeedback {
    private int feedbackId;
    private int patientId;
    private int doctorId;
    private int rating;
    private String comments;

    public PatientFeedback(int feedbackId, int patientId, int doctorId, int rating, String comments) {
        this.feedbackId = feedbackId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.rating = rating;
        this.comments = comments;
    }

    public PatientFeedback(int patientId, int doctorId, int rating, String comments) {
        this(0, patientId, doctorId, rating, comments);
    }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
