package com.example.medicalappointment.model;

/**
 * Surgeon class demonstrating:
 * - Inheritance: Subclass of Doctor.
 * - Polymorphism: High intensity surgical on-call & fee calculations.
 */
public class Surgeon extends Doctor {

    // Encapsulation - Specific field
    private String operatingTheaterId;

    public Surgeon() {
        super();
        setDoctorType("Surgeon");
        this.operatingTheaterId = "OT-1";
    }

    public Surgeon(String doctorId, String name, String specialization, String contactNumber,
                   String availabilityStatus, double consultationFee, String operatingTheaterId) {
        super(doctorId, name, specialization, contactNumber, availabilityStatus, "Surgeon", consultationFee);
        this.operatingTheaterId = operatingTheaterId;
    }

    // Polymorphism - Surgical consultations have specific procedure charges
    @Override
    public double calculateConsultationFee(boolean isEmergency) {
        double fee = getConsultationFee() + 1200.0; // Surgical equipment charge
        if (isEmergency) {
            fee *= 1.40; // 40% urgent surgical triage surcharge
        }
        return fee;
    }

    @Override
    public String getRoleDescription() {
        return "Lead Surgeon (" + getSpecialization() + ") - Assigned OT: " + operatingTheaterId;
    }

    @Override
    protected String getDoctorExtraData() {
        return "OT:" + operatingTheaterId;
    }

    public String getOperatingTheaterId() {
        return operatingTheaterId;
    }

    public void setOperatingTheaterId(String operatingTheaterId) {
        this.operatingTheaterId = operatingTheaterId;
    }
}
