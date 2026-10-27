package com.example.medicalappointment.model;

/**
 * RegularPatient class demonstrating:
 * - Inheritance: Inherits common properties from Patient.
 * - Polymorphism: Overrides calculateDiscount and getPatientTier methods.
 */
public class RegularPatient extends Patient {

    // Encapsulation - Specific attribute for Regular Patient
    private int loyaltyPoints;

    public RegularPatient() {
        super();
        setPatientType("Regular");
        this.loyaltyPoints = 0;
    }

    public RegularPatient(String patientId, String username, String password, String name,
                          String email, String phone, String address, int loyaltyPoints) {
        super(patientId, username, password, name, email, phone, address, "Regular");
        this.loyaltyPoints = loyaltyPoints;
    }

    // Polymorphism - Overriding discount calculation
    // Regular patients receive a small $5 discount if they have 100+ loyalty points
    @Override
    public double calculateDiscount(double billAmount) {
        if (loyaltyPoints >= 100) {
            return 5.0; // Flat discount for loyal regular patients
        }
        return 0.0;
    }

    // Polymorphism - Overriding tier description
    @Override
    public String getPatientTier() {
        return "Regular Patient (Loyalty Points: " + loyaltyPoints + ")";
    }

    @Override
    protected String getExtraData() {
        return "Points:" + loyaltyPoints;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }
}
