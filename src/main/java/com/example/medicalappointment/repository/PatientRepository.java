package com.example.medicalappointment.repository;

import com.example.medicalappointment.model.Patient;
import com.example.medicalappointment.model.RegularPatient;
import com.example.medicalappointment.model.VIPPatient;
import com.example.medicalappointment.util.FileHandler;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * PatientRepository
 * Demonstrates:
 * - CRUD operations for Patient entity using File I/O
 * - Polymorphic instantiation based on stored patientType
 * - File read/write against 'data/patients.txt'
 */
@Repository
public class PatientRepository {

    private static final String FILE_PATH = "data/patients.txt";

    // ==========================================
    // CRUD - Read (Find all patients)
    // ==========================================
    public List<Patient> findAll() {
        // File Handling - Read lines using FileHandler
        List<String> lines = FileHandler.readAllLines(FILE_PATH);
        List<Patient> patients = new ArrayList<>();

        for (String line : lines) {
            try {
                String[] parts = FileHandler.splitLine(line);
                if (parts.length >= 8) {
                    String patientId = parts[0].trim();
                    String username = parts[1].trim();
                    String password = parts[2].trim();
                    String name = parts[3].trim();
                    String email = parts[4].trim();
                    String phone = parts[5].trim();
                    String address = parts[6].trim();
                    String type = parts[7].trim();
                    String extra = (parts.length > 8) ? parts[8].trim() : "";

                    // Polymorphism & Inheritance - Instantiate appropriate subclass
                    if ("VIP".equalsIgnoreCase(type)) {
                        String membership = "Gold";
                        double discountRate = 0.15;
                        boolean priority = true;

                        // Parse extra metadata if available
                        if (!extra.isEmpty() && extra.contains(";")) {
                            for (String token : extra.split(";")) {
                                if (token.startsWith("Level:")) membership = token.substring(6);
                                else if (token.startsWith("Discount:")) {
                                    try { discountRate = Double.parseDouble(token.substring(9)); } catch (Exception ignored) {}
                                } else if (token.startsWith("Priority:")) {
                                    priority = Boolean.parseBoolean(token.substring(9));
                                }
                            }
                        }
                        patients.add(new VIPPatient(patientId, username, password, name, email, phone, address, membership, discountRate, priority));
                    } else {
                        int points = 0;
                        if (extra.startsWith("Points:")) {
                            try { points = Integer.parseInt(extra.substring(7)); } catch (Exception ignored) {}
                        }
                        patients.add(new RegularPatient(patientId, username, password, name, email, phone, address, points));
                    }
                }
            } catch (Exception ex) {
                System.err.println("[PatientRepository] Skipping invalid record: " + line + " - " + ex.getMessage());
            }
        }
        return patients;
    }

    // ==========================================
    // CRUD - Read (Find patient by ID)
    // ==========================================
    public Optional<Patient> findById(String patientId) {
        if (patientId == null) return Optional.empty();
        return findAll().stream()
                .filter(p -> patientId.equalsIgnoreCase(p.getPatientId()))
                .findFirst();
    }

    // ==========================================
    // CRUD - Read (Find patient by Username)
    // ==========================================
    public Optional<Patient> findByUsername(String username) {
        if (username == null) return Optional.empty();
        return findAll().stream()
                .filter(p -> username.equalsIgnoreCase(p.getUsername()))
                .findFirst();
    }

    // ==========================================
    // CRUD - Create (Save new patient)
    // ==========================================
    public synchronized Patient save(Patient patient) {
        List<Patient> existing = findAll();
        // Prevent duplicate ID
        boolean exists = existing.stream().anyMatch(p -> p.getPatientId().equalsIgnoreCase(patient.getPatientId()));
        if (exists) {
            return update(patient);
        }

        // File Handling - Append new patient line to file
        FileHandler.appendLine(FILE_PATH, patient.toFileString());
        return patient;
    }

    // ==========================================
    // CRUD - Update (Update existing patient)
    // ==========================================
    public synchronized Patient update(Patient updatedPatient) {
        List<Patient> all = findAll();
        List<String> newLines = new ArrayList<>();
        boolean found = false;

        for (Patient p : all) {
            if (p.getPatientId().equalsIgnoreCase(updatedPatient.getPatientId())) {
                newLines.add(updatedPatient.toFileString());
                found = true;
            } else {
                newLines.add(p.toFileString());
            }
        }

        if (found) {
            // File Handling - Write all updated lines back to file
            FileHandler.writeAllLines(FILE_PATH, newLines);
            return updatedPatient;
        } else {
            return save(updatedPatient);
        }
    }

    // ==========================================
    // CRUD - Delete (Delete patient by ID)
    // ==========================================
    public synchronized boolean deleteById(String patientId) {
        List<Patient> all = findAll();
        List<String> remainingLines = new ArrayList<>();
        boolean removed = false;

        for (Patient p : all) {
            if (p.getPatientId().equalsIgnoreCase(patientId)) {
                removed = true;
            } else {
                remainingLines.add(p.toFileString());
            }
        }

        if (removed) {
            // File Handling - Overwrite file with remaining lines
            FileHandler.writeAllLines(FILE_PATH, remainingLines);
        }
        return removed;
    }
}
