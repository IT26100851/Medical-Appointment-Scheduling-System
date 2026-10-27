package com.example.medicalappointment.repository;

import com.example.medicalappointment.model.Appointment;
import com.example.medicalappointment.model.EmergencyAppointment;
import com.example.medicalappointment.model.FollowUpAppointment;
import com.example.medicalappointment.util.FileHandler;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * AppointmentRepository
 * Demonstrates:
 * - CRUD operations for Appointment entity using File I/O
 * - Polymorphic instantiation of EmergencyAppointment and FollowUpAppointment
 * - Querying appointments by patient, doctor, and date/time for conflict detection
 * - File read/write against 'data/appointments.txt'
 */
@Repository
public class AppointmentRepository {

    private static final String FILE_PATH = "data/appointments.txt";

    // ==========================================
    // CRUD - Read (Find all appointments)
    // ==========================================
    public List<Appointment> findAll() {
        List<String> lines = FileHandler.readAllLines(FILE_PATH);
        List<Appointment> appointments = new ArrayList<>();

        for (String line : lines) {
            try {
                String[] parts = FileHandler.splitLine(line);
                if (parts.length >= 8) {
                    String appointmentId = parts[0].trim();
                    String patientId = parts[1].trim();
                    String doctorId = parts[2].trim();
                    String appointmentDate = parts[3].trim();
                    String appointmentTime = parts[4].trim();
                    String status = parts[5].trim();
                    String reason = parts[6].trim();
                    String appointmentType = parts[7].trim();
                    String extra = (parts.length > 8) ? parts[8].trim() : "";

                    // Polymorphism & Inheritance
                    if ("Emergency".equalsIgnoreCase(appointmentType)) {
                        String severity = "High";
                        if (extra.startsWith("Severity:")) {
                            severity = extra.substring(9);
                        }
                        appointments.add(new EmergencyAppointment(appointmentId, patientId, doctorId,
                                appointmentDate, appointmentTime, status, reason, severity));
                    } else if ("FollowUp".equalsIgnoreCase(appointmentType)) {
                        String prevId = "None";
                        if (extra.startsWith("PrevApt:")) {
                            prevId = extra.substring(8);
                        }
                        appointments.add(new FollowUpAppointment(appointmentId, patientId, doctorId,
                                appointmentDate, appointmentTime, status, reason, prevId));
                    } else {
                        appointments.add(new Appointment(appointmentId, patientId, doctorId,
                                appointmentDate, appointmentTime, status, reason, appointmentType));
                    }
                }
            } catch (Exception ex) {
                System.err.println("[AppointmentRepository] Skipping invalid record: " + line + " - " + ex.getMessage());
            }
        }
        return appointments;
    }

    // ==========================================
    // CRUD - Read (Find appointment by ID)
    // ==========================================
    public Optional<Appointment> findById(String appointmentId) {
        if (appointmentId == null) return Optional.empty();
        return findAll().stream()
                .filter(a -> appointmentId.equalsIgnoreCase(a.getAppointmentId()))
                .findFirst();
    }

    // ==========================================
    // Query - Find appointments by Patient ID
    // ==========================================
    public List<Appointment> findByPatientId(String patientId) {
        if (patientId == null) return new ArrayList<>();
        return findAll().stream()
                .filter(a -> patientId.equalsIgnoreCase(a.getPatientId()))
                .collect(Collectors.toList());
    }

    // ==========================================
    // Query - Find appointments by Doctor ID
    // ==========================================
    public List<Appointment> findByDoctorId(String doctorId) {
        if (doctorId == null) return new ArrayList<>();
        return findAll().stream()
                .filter(a -> doctorId.equalsIgnoreCase(a.getDoctorId()))
                .collect(Collectors.toList());
    }

    // ==========================================
    // Query - Find conflicting appointment for Doctor at Date & Time
    // ==========================================
    public Optional<Appointment> findActiveByDoctorDateTime(String doctorId, String date, String time) {
        return findAll().stream()
                .filter(a -> doctorId.equalsIgnoreCase(a.getDoctorId()))
                .filter(a -> date.equalsIgnoreCase(a.getAppointmentDate()))
                .filter(a -> time.equalsIgnoreCase(a.getAppointmentTime()))
                .filter(a -> !"Cancelled".equalsIgnoreCase(a.getStatus()))
                .findFirst();
    }

    // ==========================================
    // CRUD - Create (Save new appointment)
    // ==========================================
    public synchronized Appointment save(Appointment appointment) {
        List<Appointment> existing = findAll();
        boolean exists = existing.stream().anyMatch(a -> a.getAppointmentId().equalsIgnoreCase(appointment.getAppointmentId()));
        if (exists) {
            return update(appointment);
        }

        FileHandler.appendLine(FILE_PATH, appointment.toFileString());
        return appointment;
    }

    // ==========================================
    // CRUD - Update (Update existing appointment)
    // ==========================================
    public synchronized Appointment update(Appointment updatedAppointment) {
        List<Appointment> all = findAll();
        List<String> newLines = new ArrayList<>();
        boolean found = false;

        for (Appointment a : all) {
            if (a.getAppointmentId().equalsIgnoreCase(updatedAppointment.getAppointmentId())) {
                newLines.add(updatedAppointment.toFileString());
                found = true;
            } else {
                newLines.add(a.toFileString());
            }
        }

        if (found) {
            FileHandler.writeAllLines(FILE_PATH, newLines);
            return updatedAppointment;
        } else {
            return save(updatedAppointment);
        }
    }

    // ==========================================
    // CRUD - Delete (Delete appointment by ID)
    // ==========================================
    public synchronized boolean deleteById(String appointmentId) {
        List<Appointment> all = findAll();
        List<String> remainingLines = new ArrayList<>();
        boolean removed = false;

        for (Appointment a : all) {
            if (a.getAppointmentId().equalsIgnoreCase(appointmentId)) {
                removed = true;
            } else {
                remainingLines.add(a.toFileString());
            }
        }

        if (removed) {
            FileHandler.writeAllLines(FILE_PATH, remainingLines);
        }
        return removed;
    }
}
