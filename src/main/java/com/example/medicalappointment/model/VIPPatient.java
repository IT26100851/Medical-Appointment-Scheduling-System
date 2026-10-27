package com.example.medicalappointment.model;

/**
 * VIPPatient class demonstrating:
 * - Inheritance: Extends Patient.
 * - Polymorphism: Custom implementation of calculateDiscount (percentage-based discount)
 *   and customized tier description.
 */
public class VIPPatient extends Patient {

    // Encapsulation - VIP specific fields
    private String membershipLevel;  // "Silver", "Gold", "Platinum"
    private double discountRate;     // e.g. 0.15 for 15% discount
    private boolean priorityBooking;

    public VIPPatient() {
        super();
        setPatientType("VIP");
        this.membershipLevel = "Gold";
        this.discountRate = 0.15;
        this.priorityBooking = true;
    }

    public VIPPatient(String patientId, String username, String password, String name,
                      String email, String phone, String address,
                      String membershipLevel, double discountRate, boolean priorityBooking) {
        super(patientId, username, password, name, email, phone, address, "VIP");
        this.membershipLevel = membershipLevel;
        this.discountRate = discountRate;
        this.priorityBooking = priorityBooking;
    }

    // Polymorphism - Overridden method: VIP patients receive a percentage discount on their bills
    @Override
    public double calculateDiscount(double billAmount) {
        return billAmount * discountRate;
    }

    // Polymorphism - Overridden method: Displays VIP status and membership tier
    @Override
    public String getPatientTier() {
        return "VIP " + membershipLevel + " Patient (" + (int)(discountRate * 100) + "% Discount)";
    }

    @Override
    protected String getExtraData() {
        return "Level:" + membershipLevel + ";Discount:" + discountRate + ";Priority:" + priorityBooking;
    }

    // Getters and Setters
    public String getMembershipLevel() {
        return membershipLevel;
    }

    public void setMembershipLevel(String membershipLevel) {
        this.membershipLevel = membershipLevel;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    public boolean isPriorityBooking() {
        return priorityBooking;
    }

    public void setPriorityBooking(boolean priorityBooking) {
        this.priorityBooking = priorityBooking;
    }
}
