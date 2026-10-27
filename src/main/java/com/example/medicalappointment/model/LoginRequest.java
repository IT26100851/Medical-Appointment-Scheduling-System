package com.example.medicalappointment.model;

/**
 * Encapsulated DTO for user login requests
 */
public class LoginRequest {
    private String username;
    private String password;
    private String role; // "PATIENT", "ADMIN", "STAFF"

    public LoginRequest() {
    }

    public LoginRequest(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
