package com.example.medicalappointment.model;

/**
 * Review class demonstrating:
 * - Encapsulation: Private member variables with getters/setters.
 * - Inheritance: Subclassed by PatientReview and VerifiedReview.
 * - Polymorphism: Dynamic badge labeling and verification check.
 */
public class Review {

    // Encapsulation - Private fields
    private String reviewId;
    private String patientId;
    private String doctorId;
    private int rating; // 1 to 5
    private String comment;
    private String date; // YYYY-MM-DD
    private String reviewType; // "PatientReview" or "VerifiedReview"

    public Review() {
        this.rating = 5;
        this.reviewType = "PatientReview";
    }

    public Review(String reviewId, String patientId, String doctorId, int rating,
                  String comment, String date, String reviewType) {
        this.reviewId = reviewId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.rating = (rating < 1) ? 1 : Math.min(rating, 5);
        this.comment = comment;
        this.date = date;
        this.reviewType = (reviewType == null || reviewType.isBlank()) ? "PatientReview" : reviewType;
    }

    // Polymorphism - Overridden in subclasses
    public String getBadgeLabel() {
        return "Community Review";
    }

    // Polymorphism - Indicates if the review was posted by a verified treated patient
    public boolean isVerifiedPatient() {
        return false;
    }

    // File Handling serialization for reviews.txt
    public String toFileString() {
        return String.join("|",
                safe(reviewId),
                safe(patientId),
                safe(doctorId),
                String.valueOf(rating),
                safe(comment),
                safe(date),
                safe(reviewType),
                safe(getExtraReviewData())
        );
    }

    protected String getExtraReviewData() {
        return "None";
    }

    protected String safe(String val) {
        return (val == null) ? "" : val.replace("|", " ").trim();
    }

    // Getters and Setters
    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = (rating < 1) ? 1 : Math.min(rating, 5);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReviewType() {
        return reviewType;
    }

    public void setReviewType(String reviewType) {
        this.reviewType = reviewType;
    }
}
