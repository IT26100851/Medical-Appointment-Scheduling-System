package com.example.medicalappointment.model;

/**
 * Receptionist class demonstrating:
 * - Inheritance: Extends Staff.
 * - Polymorphism: Implements getRoleDescription and calculateDutyAllowance.
 */
public class Receptionist extends Staff {

    // Encapsulation - Specific fields
    private String deskNumber;
    private String languageProficiency;

    public Receptionist() {
        super();
        setPosition("Receptionist");
    }

    public Receptionist(String staffId, String name, String contactNumber, String department,
                        double salary, String deskNumber, String languageProficiency) {
        super(staffId, name, contactNumber, "Receptionist", department, salary);
        this.deskNumber = deskNumber;
        this.languageProficiency = languageProficiency;
    }

    @Override
    public String getRoleDescription() {
        return "Patient Receptionist - Counter " + deskNumber + " (Languages: " + languageProficiency + ")";
    }

    @Override
    public double calculateDutyAllowance() {
        return 3000.0; // Flat customer relations duty allowance
    }

    @Override
    protected String getStaffExtraDetails() {
        return "Desk:" + deskNumber + ";Languages:" + languageProficiency;
    }

    public String getDeskNumber() {
        return deskNumber;
    }

    public void setDeskNumber(String deskNumber) {
        this.deskNumber = deskNumber;
    }

    public String getLanguageProficiency() {
        return languageProficiency;
    }

    public void setLanguageProficiency(String languageProficiency) {
        this.languageProficiency = languageProficiency;
    }
}
