package com.example.medicalappointment.repository;

import com.example.medicalappointment.model.Doctor;
import com.example.medicalappointment.model.GeneralDoctor;
import com.example.medicalappointment.model.Nurse;
import com.example.medicalappointment.model.Receptionist;
import com.example.medicalappointment.model.Staff;
import com.example.medicalappointment.util.FileHandler;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * StaffRepository
 * Demonstrates:
 * - CRUD operations for Staff entity using File I/O
 * - Polymorphic instantiation of Doctor, Nurse, and Receptionist subclasses
 * - File read/write against 'data/staff.txt'
 */
@Repository
public class StaffRepository {

    private static final String FILE_PATH = "data/staff.txt";

    // ==========================================
    // CRUD - Read (Find all staff)
    // ==========================================
    public List<Staff> findAll() {
        List<String> lines = FileHandler.readAllLines(FILE_PATH);
        List<Staff> staffList = new ArrayList<>();

        for (String line : lines) {
            try {
                String[] parts = FileHandler.splitLine(line);
                if (parts.length >= 6) {
                    String staffId = parts[0].trim();
                    String name = parts[1].trim();
                    String contactNumber = parts[2].trim();
                    String position = parts[3].trim();
                    String department = parts[4].trim();
                    double salary = Double.parseDouble(parts[5].trim());
                    String extra = (parts.length > 6) ? parts[6].trim() : "";

                    // Polymorphism & Inheritance
                    if ("Nurse".equalsIgnoreCase(position)) {
                        String ward = "General Ward";
                        String shift = "Day";
                        if (!extra.isEmpty() && extra.contains(";")) {
                            for (String token : extra.split(";")) {
                                if (token.startsWith("Ward:")) ward = token.substring(5);
                                else if (token.startsWith("Shift:")) shift = token.substring(6);
                            }
                        }
                        staffList.add(new Nurse(staffId, name, contactNumber, department, salary, ward, shift));
                    } else if ("Receptionist".equalsIgnoreCase(position)) {
                        String desk = "Desk 01";
                        String lang = "English, Sinhala";
                        if (!extra.isEmpty() && extra.contains(";")) {
                            for (String token : extra.split(";")) {
                                if (token.startsWith("Desk:")) desk = token.substring(5);
                                else if (token.startsWith("Languages:")) lang = token.substring(10);
                            }
                        }
                        staffList.add(new Receptionist(staffId, name, contactNumber, department, salary, desk, lang));
                    } else { // Doctor staff
                        String status = "Available";
                        if (!extra.isEmpty() && extra.contains("Status:")) {
                            status = extra.substring(extra.indexOf("Status:") + 7);
                        }
                        staffList.add(new GeneralDoctor(staffId, name, department, contactNumber, status, 2500.0, "Consulting Room"));
                    }
                }
            } catch (Exception ex) {
                System.err.println("[StaffRepository] Skipping invalid record: " + line + " - " + ex.getMessage());
            }
        }
        return staffList;
    }

    // ==========================================
    // CRUD - Read (Find staff by ID)
    // ==========================================
    public Optional<Staff> findById(String staffId) {
        if (staffId == null) return Optional.empty();
        return findAll().stream()
                .filter(s -> staffId.equalsIgnoreCase(s.getStaffId()))
                .findFirst();
    }

    // ==========================================
    // Search - Find staff by Position
    // ==========================================
    public List<Staff> findByPosition(String position) {
        if (position == null || position.isBlank()) return findAll();
        return findAll().stream()
                .filter(s -> s.getPosition().equalsIgnoreCase(position))
                .collect(Collectors.toList());
    }

    // ==========================================
    // CRUD - Create (Save new staff)
    // ==========================================
    public synchronized Staff save(Staff staff) {
        List<Staff> existing = findAll();
        boolean exists = existing.stream().anyMatch(s -> s.getStaffId().equalsIgnoreCase(staff.getStaffId()));
        if (exists) {
            return update(staff);
        }

        FileHandler.appendLine(FILE_PATH, staff.toStaffFileString());
        return staff;
    }

    // ==========================================
    // CRUD - Update (Update existing staff)
    // ==========================================
    public synchronized Staff update(Staff updatedStaff) {
        List<Staff> all = findAll();
        List<String> newLines = new ArrayList<>();
        boolean found = false;

        for (Staff s : all) {
            if (s.getStaffId().equalsIgnoreCase(updatedStaff.getStaffId())) {
                newLines.add(updatedStaff.toStaffFileString());
                found = true;
            } else {
                newLines.add(s.toStaffFileString());
            }
        }

        if (found) {
            FileHandler.writeAllLines(FILE_PATH, newLines);
            return updatedStaff;
        } else {
            return save(updatedStaff);
        }
    }

    // ==========================================
    // CRUD - Delete (Delete staff by ID)
    // ==========================================
    public synchronized boolean deleteById(String staffId) {
        List<Staff> all = findAll();
        List<String> remainingLines = new ArrayList<>();
        boolean removed = false;

        for (Staff s : all) {
            if (s.getStaffId().equalsIgnoreCase(staffId)) {
                removed = true;
            } else {
                remainingLines.add(s.toStaffFileString());
            }
        }

        if (removed) {
            FileHandler.writeAllLines(FILE_PATH, remainingLines);
        }
        return removed;
    }
}
