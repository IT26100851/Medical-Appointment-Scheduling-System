package com.example.medicalappointment.model;

/**
 * GeneralDoctor class demonstrating:
 * - Inheritance: Subclass of Doctor.
 * - Polymorphism: Custom consultation fee logic and role description.
 */
public class GeneralDoctor extends Doctor {

    // Encapsulation - Specific field
    private String clinicRoomNumber;

    public GeneralDoctor() {
        super();
        setDoctorType("GeneralDoctor");
        this.clinicRoomNumber = "Room 101";
    }

    public GeneralDoctor(String doctorId, String name, String specialization, String contactNumber,
                         String availabilityStatus, double consultationFee, String clinicRoomNumber) {
        super(doctorId, name, specialization, contactNumber, availabilityStatus, "GeneralDoctor", consultationFee);
        this.clinicRoomNumber = clinicRoomNumber;
    }

    // Polymorphism - General doctors charge regular fees without heavy emergency markups
    @Override
    public double calculateConsultationFee(boolean isEmergency) {
        if (isEmergency) {
            return getConsultationFee() + 300.0;
        }
        return getConsultationFee();
    }

    @Override
    public String getRoleDescription() {
        return "General Practitioner (OPD & Family Medicine) - Room " + clinicRoomNumber;
    }

    @Override
    protected String getDoctorExtraData() {
        return "ClinicRoom:" + clinicRoomNumber;
    }

    public String getClinicRoomNumber() {
        return clinicRoomNumber;
    }

    public void setClinicRoomNumber(String clinicRoomNumber) {
        this.clinicRoomNumber = clinicRoomNumber;
    }
}
