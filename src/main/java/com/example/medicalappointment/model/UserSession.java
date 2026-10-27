package com.example.medicalappointment.model;

/**
 * Encapsulated user session representation
 */
public class UserSession {
    private String userId;
    private String username;
    private String name;
    private String role; // "PATIENT", "ADMIN", "STAFF"
    private String email;
    private String patientType; // "Regular" or "VIP" if patient

    public UserSession() {
    }

    public UserSession(String userId, String username, String name, String role, String email, String patientType) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.role = role;
        this.email = email;
        this.patientType = patientType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPatientType() {
        return patientType;
    }

    public void setPatientType(String patientType) {
        this.patientType = patientType;
    }
}
