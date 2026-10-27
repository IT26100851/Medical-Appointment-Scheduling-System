package com.example.medicalappointment.model;

/**
 * SpecialistDoctor class demonstrating:
 * - Inheritance: Subclass of Doctor.
 * - Polymorphism: Specialist fee calculation with specialist overhead.
 */
public class SpecialistDoctor extends Doctor {

    // Encapsulation - Specific field
    private String boardCertification;

    public SpecialistDoctor() {
        super();
        setDoctorType("SpecialistDoctor");
        this.boardCertification = "Consultant Specialist";
    }

    public SpecialistDoctor(String doctorId, String name, String specialization, String contactNumber,
                            String availabilityStatus, double consultationFee, String boardCertification) {
        super(doctorId, name, specialization, contactNumber, availabilityStatus, "SpecialistDoctor", consultationFee);
        this.boardCertification = boardCertification;
    }

    // Polymorphism - Specialist surcharge applied
    @Override
    public double calculateConsultationFee(boolean isEmergency) {
        double fee = getConsultationFee() + 500.0; // Specialist equipment & expertise charge
        if (isEmergency) {
            fee *= 1.25; // 25% emergency surcharge
        }
        return fee;
    }

    @Override
    public String getRoleDescription() {
        return "Board-Certified Specialist (" + boardCertification + ") in " + getSpecialization();
    }

    @Override
    protected String getDoctorExtraData() {
        return "BoardCertification:" + boardCertification;
    }

    public String getBoardCertification() {
        return boardCertification;
    }

    public void setBoardCertification(String boardCertification) {
        this.boardCertification = boardCertification;
    }
}
