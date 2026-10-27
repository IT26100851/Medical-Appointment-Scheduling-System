package com.example.medicalappointment.repository;

import com.example.medicalappointment.model.Doctor;
import com.example.medicalappointment.model.GeneralDoctor;
import com.example.medicalappointment.model.SpecialistDoctor;
import com.example.medicalappointment.model.Surgeon;
import com.example.medicalappointment.util.FileHandler;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DoctorRepository
 * Demonstrates:
 * - CRUD operations for Doctor entity using File I/O
 * - Polymorphic instantiation of GeneralDoctor, SpecialistDoctor, and Surgeon
 * - File read/write against 'data/doctors.txt'
 */
@Repository
public class DoctorRepository {

    private static final String FILE_PATH = "data/doctors.txt";

    // ==========================================
    // CRUD - Read (Find all doctors)
    // ==========================================
    public List<Doctor> findAll() {
        List<String> lines = FileHandler.readAllLines(FILE_PATH);
        List<Doctor> doctors = new ArrayList<>();

        for (String line : lines) {
            try {
                String[] parts = FileHandler.splitLine(line);
                if (parts.length >= 7) {
                    String doctorId = parts[0].trim();
                    String name = parts[1].trim();
                    String specialization = parts[2].trim();
                    String contactNumber = parts[3].trim();
                    String availabilityStatus = parts[4].trim();
                    String doctorType = parts[5].trim();
                    double fee = 2500.0;
                    try {
                        fee = Double.parseDouble(parts[6].trim());
                    } catch (Exception ignored) {}

                    String extra = (parts.length > 7) ? parts[7].trim() : "";

                    // Polymorphism & Inheritance - Instantiate correct subclass based on stored type
                    if ("SpecialistDoctor".equalsIgnoreCase(doctorType)) {
                        String cert = "Consultant Specialist";
                        if (extra.startsWith("BoardCertification:")) {
                            cert = extra.substring(19);
                        }
                        doctors.add(new SpecialistDoctor(doctorId, name, specialization, contactNumber, availabilityStatus, fee, cert));
                    } else if ("Surgeon".equalsIgnoreCase(doctorType)) {
                        String ot = "OT-1";
                        if (extra.startsWith("OT:")) {
                            ot = extra.substring(3);
                        }
                        doctors.add(new Surgeon(doctorId, name, specialization, contactNumber, availabilityStatus, fee, ot));
                    } else {
                        String room = "Room 101";
                        if (extra.startsWith("ClinicRoom:")) {
                            room = extra.substring(11);
                        }
                        doctors.add(new GeneralDoctor(doctorId, name, specialization, contactNumber, availabilityStatus, fee, room));
                    }
                }
            } catch (Exception ex) {
                System.err.println("[DoctorRepository] Skipping invalid record: " + line + " - " + ex.getMessage());
            }
        }
        return doctors;
    }

    // ==========================================
    // CRUD - Read (Find doctor by ID)
    // ==========================================
    public Optional<Doctor> findById(String doctorId) {
        if (doctorId == null) return Optional.empty();
        return findAll().stream()
                .filter(d -> doctorId.equalsIgnoreCase(d.getDoctorId()))
                .findFirst();
    }

    // ==========================================
    // Search - Find doctors by Specialization
    // ==========================================
    public List<Doctor> findBySpecialization(String specialization) {
        if (specialization == null || specialization.isBlank()) return findAll();
        return findAll().stream()
                .filter(d -> d.getSpecialization().toLowerCase().contains(specialization.toLowerCase()))
                .collect(Collectors.toList());
    }

    // ==========================================
    // Search - Find doctors by Availability Status
    // ==========================================
    public List<Doctor> findByAvailabilityStatus(String status) {
        if (status == null || status.isBlank()) return findAll();
        return findAll().stream()
                .filter(d -> d.getAvailabilityStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    // ==========================================
    // CRUD - Create (Save new doctor)
    // ==========================================
    public synchronized Doctor save(Doctor doctor) {
        List<Doctor> existing = findAll();
        boolean exists = existing.stream().anyMatch(d -> d.getDoctorId().equalsIgnoreCase(doctor.getDoctorId()));
        if (exists) {
            return update(doctor);
        }

        FileHandler.appendLine(FILE_PATH, doctor.toDoctorFileString());
        return doctor;
    }

    // ==========================================
    // CRUD - Update (Update existing doctor)
    // ==========================================
    public synchronized Doctor update(Doctor updatedDoctor) {
        List<Doctor> all = findAll();
        List<String> newLines = new ArrayList<>();
        boolean found = false;

        for (Doctor d : all) {
            if (d.getDoctorId().equalsIgnoreCase(updatedDoctor.getDoctorId())) {
                newLines.add(updatedDoctor.toDoctorFileString());
                found = true;
            } else {
                newLines.add(d.toDoctorFileString());
            }
        }

        if (found) {
            FileHandler.writeAllLines(FILE_PATH, newLines);
            return updatedDoctor;
        } else {
            return save(updatedDoctor);
        }
    }

    // ==========================================
    // CRUD - Delete (Delete doctor by ID)
    // ==========================================
    public synchronized boolean deleteById(String doctorId) {
        List<Doctor> all = findAll();
        List<String> remainingLines = new ArrayList<>();
        boolean removed = false;

        for (Doctor d : all) {
            if (d.getDoctorId().equalsIgnoreCase(doctorId)) {
                removed = true;
            } else {
                remainingLines.add(d.toDoctorFileString());
            }
        }

        if (removed) {
            FileHandler.writeAllLines(FILE_PATH, remainingLines);
        }
        return removed;
    }
}
