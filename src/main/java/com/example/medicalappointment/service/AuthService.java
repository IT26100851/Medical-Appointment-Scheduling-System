package com.example.medicalappointment.service;

import com.example.medicalappointment.exception.ValidationException;
import com.example.medicalappointment.model.LoginRequest;
import com.example.medicalappointment.model.Patient;
import com.example.medicalappointment.model.UserSession;
import org.springframework.stereotype.Service;

/**
 * Simple role-based authentication service suitable for university project demonstration.
 * Roles: PATIENT, ADMIN, STAFF
 */
@Service
public class AuthService {

    private final PatientService patientService;

    public AuthService(PatientService patientService) {
        this.patientService = patientService;
    }

    public UserSession authenticate(LoginRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ValidationException("Username and password cannot be empty.");
        }

        String role = (request.getRole() != null) ? request.getRole().toUpperCase().trim() : "PATIENT";
        String username = request.getUsername().trim();
        String password = request.getPassword().trim();

        if ("ADMIN".equals(role)) {
            if ("admin".equalsIgnoreCase(username) && "admin123".equals(password)) {
                return new UserSession("ADM-001", "admin", "System Administrator", "ADMIN", "admin@hospital.com", null);
            } else {
                throw new ValidationException("Invalid Admin credentials. Use admin / admin123");
            }
        }

        if ("STAFF".equals(role)) {
            if ("staff".equalsIgnoreCase(username) && "staff123".equals(password)) {
                return new UserSession("STF-101", "staff", "Nurse Mary Watson", "STAFF", "staff@hospital.com", null);
            } else {
                throw new ValidationException("Invalid Staff credentials. Use staff / staff123");
            }
        }

        // Default: Authenticate Patient from file storage
        Patient patient = patientService.login(username, password);
        return new UserSession(patient.getPatientId(), patient.getUsername(), patient.getName(),
                "PATIENT", patient.getEmail(), patient.getPatientType());
    }
}
