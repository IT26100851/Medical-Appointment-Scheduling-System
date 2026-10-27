package com.example.medicalappointment.model;

/**
 * Base Patient class demonstrating Object Oriented Programming concepts:
 * - Encapsulation: Private member variables with public getters and setters.
 * - Inheritance: Extended by RegularPatient and VIPPatient.
 * - Polymorphism: Overridden methods for discount and tier calculations.
 * - Information Hiding: Internal state is protected from direct external modification.
 */
public class Patient {

    // Encapsulation - Private fields to protect internal patient state
    private String patientId;
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String patientType; // "Regular" or "VIP"

    // Default Constructor
    public Patient() {
        this.patientType = "Regular";
    }

    // Parameterized Constructor
    public Patient(String patientId, String username, String password, String name,
                   String email, String phone, String address, String patientType) {
        this.patientId = patientId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.patientType = (patientType == null || patientType.isBlank()) ? "Regular" : patientType;
    }

    // Polymorphism - Base implementation for discount calculation (can be overridden by subclasses)
    public double calculateDiscount(double billAmount) {
        return 0.0; // Default patients receive no automatic discount
    }

    // Polymorphism - Base implementation for tier description
    public String getPatientTier() {
        return "Standard Patient";
    }

    // File Handling helper - Serializes object to pipe-delimited string
    public String toFileString() {
        return String.join("|",
                safe(patientId),
                safe(username),
                safe(password),
                safe(name),
                safe(email),
                safe(phone),
                safe(address),
                safe(patientType),
                safe(getExtraData())
        );
    }

    // Protected helper for subclass-specific data serialization
    protected String getExtraData() {
        return "None";
    }

    private String safe(String val) {
        return (val == null) ? "" : val.replace("|", " ").trim();
    }

    // ==========================================
    // Encapsulation - Getters and Setters
    // ==========================================

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPatientType() {
        return patientType;
    }

    public void setPatientType(String patientType) {
        this.patientType = patientType;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "patientId='" + patientId + '\'' +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", patientType='" + patientType + '\'' +
                '}';
    }
}
