package com.example.medicalappointment.model;

/**
 * PatientReview class demonstrating:
 * - Inheritance: Subclass of Review.
 * - Polymorphism: Custom badge formatting for general patient reviews.
 */
public class PatientReview extends Review {

    public PatientReview() {
        super();
        setReviewType("PatientReview");
    }

    public PatientReview(String reviewId, String patientId, String doctorId,
                         int rating, String comment, String date) {
        super(reviewId, patientId, doctorId, rating, comment, date, "PatientReview");
    }

    @Override
    public String getBadgeLabel() {
        return "Patient Review";
    }

    @Override
    public boolean isVerifiedPatient() {
        return false;
    }

    @Override
    protected String getExtraReviewData() {
        return "Regular";
    }
}
