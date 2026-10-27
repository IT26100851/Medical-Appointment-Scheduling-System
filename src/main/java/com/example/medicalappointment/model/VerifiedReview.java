package com.example.medicalappointment.model;

/**
 * VerifiedReview class demonstrating:
 * - Inheritance: Subclass of Review.
 * - Polymorphism: Verified badge with treatment details and verified indicator.
 */
public class VerifiedReview extends Review {

    // Encapsulation - Specific fields for verified consultations
    private String appointmentId;
    private String verifiedTreatment;

    public VerifiedReview() {
        super();
        setReviewType("VerifiedReview");
        this.appointmentId = "";
        this.verifiedTreatment = "Clinical Consultation";
    }

    public VerifiedReview(String reviewId, String patientId, String doctorId,
                          int rating, String comment, String date,
                          String appointmentId, String verifiedTreatment) {
        super(reviewId, patientId, doctorId, rating, comment, date, "VerifiedReview");
        this.appointmentId = appointmentId;
        this.verifiedTreatment = verifiedTreatment;
    }

    @Override
    public String getBadgeLabel() {
        return "Verified Patient (" + (verifiedTreatment != null ? verifiedTreatment : "Treated") + ")";
    }

    @Override
    public boolean isVerifiedPatient() {
        return true;
    }

    @Override
    protected String getExtraReviewData() {
        return "Apt:" + appointmentId + ";Treatment:" + verifiedTreatment;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getVerifiedTreatment() {
        return verifiedTreatment;
    }

    public void setVerifiedTreatment(String verifiedTreatment) {
        this.verifiedTreatment = verifiedTreatment;
    }
}
