package com.example.medicalappointment.model;

/**
 * Base Doctor class demonstrating:
 * - Inheritance: Extends Staff (representing clinical staff).
 * - Multi-level Inheritance: Extended by GeneralDoctor, SpecialistDoctor, and Surgeon.
 * - Encapsulation: Private fields with accessor/mutator methods.
 * - Abstraction & Polymorphism: Implements Staff methods and provides polymorphic
 *   consultation fee calculation.
 */
public class Doctor extends Staff {

    // Encapsulation - Doctor specific fields
    private String doctorId;
    private String specialization;
    private String availabilityStatus; // "Available", "Busy", "On Leave"
    private String doctorType;         // "GeneralDoctor", "SpecialistDoctor", "Surgeon"
    private double consultationFee;

    public Doctor() {
        super();
        setPosition("Doctor");
        this.availabilityStatus = "Available";
        this.doctorType = "GeneralDoctor";
        this.consultationFee = 2500.0;
    }

    public Doctor(String doctorId, String name, String specialization, String contactNumber,
                  String availabilityStatus, String doctorType, double consultationFee) {
        super(doctorId, name, contactNumber, "Doctor", specialization, 150000.0);
        this.doctorId = doctorId;
        this.specialization = specialization;
        this.availabilityStatus = (availabilityStatus == null || availabilityStatus.isBlank()) ? "Available" : availabilityStatus;
        this.doctorType = (doctorType == null || doctorType.isBlank()) ? "GeneralDoctor" : doctorType;
        this.consultationFee = consultationFee;
    }

    // Polymorphism - Dynamic method for calculating consultation fee based on emergency urgency
    public double calculateConsultationFee(boolean isEmergency) {
        if (isEmergency) {
            return consultationFee * 1.20; // 20% standard emergency surcharge
        }
        return consultationFee;
    }

    // Polymorphism - Overriding Staff abstract method
    @Override
    public String getRoleDescription() {
        return "Doctor (" + doctorType + ") specializing in " + specialization;
    }

    // Polymorphism - Overriding Staff duty allowance
    @Override
    public double calculateDutyAllowance() {
        return 20000.0; // Professional clinical allowance
    }

    @Override
    protected String getStaffExtraDetails() {
        return "Specialization:" + specialization + ";Status:" + availabilityStatus;
    }

    // File Handling serialization for doctors.txt
    public String toDoctorFileString() {
        return String.join("|",
                safe(doctorId),
                safe(getName()),
                safe(specialization),
                safe(getContactNumber()),
                safe(availabilityStatus),
                safe(doctorType),
                String.format("%.2f", consultationFee),
                safe(getDoctorExtraData())
        );
    }

    protected String getDoctorExtraData() {
        return "None";
    }

    // Getters and Setters
    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
        setStaffId(doctorId);
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
        setDepartment(specialization);
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public String getDoctorType() {
        return doctorType;
    }

    public void setDoctorType(String doctorType) {
        this.doctorType = doctorType;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public boolean isAvailable() {
        return "Available".equalsIgnoreCase(availabilityStatus);
    }
}
