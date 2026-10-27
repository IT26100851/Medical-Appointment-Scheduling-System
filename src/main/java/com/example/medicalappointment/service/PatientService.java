package com.example.medicalappointment.service;

import com.example.medicalappointment.exception.ConflictException;
import com.example.medicalappointment.exception.ResourceNotFoundException;
import com.example.medicalappointment.exception.ValidationException;
import com.example.medicalappointment.model.Patient;
import com.example.medicalappointment.model.RegularPatient;
import com.example.medicalappointment.model.VIPPatient;
import com.example.medicalappointment.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * PatientService
 * Demonstrates:
 * - Business logic layer for Patient operations
 * - Input validation (email, phone, required fields)
 * - CRUD operations calling PatientRepository
 * - OOP: Polymorphic handling of RegularPatient and VIPPatient
 */
@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    // ==========================================
    // CRUD - Create / Register Patient
    // ==========================================
    public Patient registerPatient(Patient patient) {
        validatePatientData(patient);

        // Check if username already exists
        if (patientRepository.findByUsername(patient.getUsername()).isPresent()) {
            throw new ConflictException("Username '" + patient.getUsername() + "' is already taken. Please choose another.");
        }

        // Generate unique patient ID if not provided
        if (patient.getPatientId() == null || patient.getPatientId().isBlank()) {
            patient.setPatientId("PAT-" + System.currentTimeMillis() % 100000);
        }

        // If VIP was requested in registration, ensure correct polymorphic type
        Patient toSave = patient;
        if ("VIP".equalsIgnoreCase(patient.getPatientType()) && !(patient instanceof VIPPatient)) {
            toSave = new VIPPatient(patient.getPatientId(), patient.getUsername(), patient.getPassword(),
                    patient.getName(), patient.getEmail(), patient.getPhone(), patient.getAddress(),
                    "Gold", 0.15, true);
        } else if (!(patient instanceof VIPPatient) && !(patient instanceof RegularPatient)) {
            toSave = new RegularPatient(patient.getPatientId(), patient.getUsername(), patient.getPassword(),
                    patient.getName(), patient.getEmail(), patient.getPhone(), patient.getAddress(), 10);
        }

        return patientRepository.save(toSave);
    }

    // ==========================================
    // Authenticate / Login Patient
    // ==========================================
    public Patient login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new ValidationException("Username and password cannot be empty.");
        }

        Patient patient = patientRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid username or password."));

        if (!patient.getPassword().equals(password)) {
            throw new ValidationException("Invalid username or password.");
        }

        return patient;
    }

    // ==========================================
    // CRUD - Read (Get all patients)
    // ==========================================
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    // ==========================================
    // CRUD - Read (Get patient by ID)
    // ==========================================
    public Patient getPatientById(String patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));
    }

    // ==========================================
    // CRUD - Read (Get patient by username)
    // ==========================================
    public Patient getPatientByUsername(String username) {
        return patientRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with username: " + username));
    }

    // ==========================================
    // Search - Filter patients by search term
    // ==========================================
    public List<Patient> searchPatients(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllPatients();
        }
        String lower = keyword.toLowerCase().trim();
        return getAllPatients().stream()
                .filter(p -> p.getPatientId().toLowerCase().contains(lower)
                        || p.getName().toLowerCase().contains(lower)
                        || p.getUsername().toLowerCase().contains(lower)
                        || p.getEmail().toLowerCase().contains(lower)
                        || p.getPhone().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    // ==========================================
    // CRUD - Update (Update patient details)
    // ==========================================
    public Patient updatePatient(String patientId, Patient updatedData) {
        Patient existing = getPatientById(patientId);

        // Validate basic fields
        if (updatedData.getName() != null && !updatedData.getName().isBlank()) {
            existing.setName(updatedData.getName().trim());
        }
        if (updatedData.getEmail() != null && !updatedData.getEmail().isBlank()) {
            if (!EMAIL_PATTERN.matcher(updatedData.getEmail()).matches()) {
                throw new ValidationException("Invalid email format.");
            }
            existing.setEmail(updatedData.getEmail().trim());
        }
        if (updatedData.getPhone() != null && !updatedData.getPhone().isBlank()) {
            existing.setPhone(updatedData.getPhone().trim());
        }
        if (updatedData.getAddress() != null) {
            existing.setAddress(updatedData.getAddress().trim());
        }
        if (updatedData.getPassword() != null && !updatedData.getPassword().isBlank()) {
            existing.setPassword(updatedData.getPassword());
        }

        // If patient type changed to VIP
        if ("VIP".equalsIgnoreCase(updatedData.getPatientType()) && !(existing instanceof VIPPatient)) {
            existing = new VIPPatient(existing.getPatientId(), existing.getUsername(), existing.getPassword(),
                    existing.getName(), existing.getEmail(), existing.getPhone(), existing.getAddress(),
                    "Gold", 0.15, true);
        } else if ("Regular".equalsIgnoreCase(updatedData.getPatientType()) && (existing instanceof VIPPatient)) {
            existing = new RegularPatient(existing.getPatientId(), existing.getUsername(), existing.getPassword(),
                    existing.getName(), existing.getEmail(), existing.getPhone(), existing.getAddress(), 20);
        }

        return patientRepository.update(existing);
    }

    // ==========================================
    // CRUD - Delete (Delete patient account)
    // ==========================================
    public boolean deletePatient(String patientId) {
        getPatientById(patientId); // Ensure exists first
        return patientRepository.deleteById(patientId);
    }

    // ==========================================
    // Validation helper
    // ==========================================
    private void validatePatientData(Patient patient) {
        if (patient.getName() == null || patient.getName().trim().isEmpty()) {
            throw new ValidationException("Patient full name is required.");
        }
        if (patient.getUsername() == null || patient.getUsername().trim().isEmpty()) {
            throw new ValidationException("Username is required.");
        }
        if (patient.getPassword() == null || patient.getPassword().length() < 3) {
            throw new ValidationException("Password must be at least 3 characters.");
        }
        if (patient.getEmail() == null || !EMAIL_PATTERN.matcher(patient.getEmail()).matches()) {
            throw new ValidationException("Please provide a valid email address.");
        }
        if (patient.getPhone() == null || patient.getPhone().trim().isEmpty()) {
            throw new ValidationException("Phone number is required.");
        }
    }
}
