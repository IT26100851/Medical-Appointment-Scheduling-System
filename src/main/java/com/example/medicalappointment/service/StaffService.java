package com.example.medicalappointment.service;

import com.example.medicalappointment.exception.ResourceNotFoundException;
import com.example.medicalappointment.exception.ValidationException;
import com.example.medicalappointment.model.Doctor;
import com.example.medicalappointment.model.GeneralDoctor;
import com.example.medicalappointment.model.Nurse;
import com.example.medicalappointment.model.Receptionist;
import com.example.medicalappointment.model.Staff;
import com.example.medicalappointment.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * StaffService
 * Demonstrates:
 * - CRUD operations for hospital staff
 * - Polymorphic instantiation and duty allowance calculations
 * - File storage in data/staff.txt
 */
@Service
public class StaffService {

    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    // ==========================================
    // CRUD - Create (Add new staff member)
    // ==========================================
    public Staff addStaff(Staff staff) {
        validateStaff(staff);

        if (staff.getStaffId() == null || staff.getStaffId().isBlank()) {
            staff.setStaffId("STF-" + (100 + staffRepository.findAll().size() + 1));
        }

        // Polymorphic creation based on position
        Staff toSave = staff;
        String pos = staff.getPosition();
        if ("Nurse".equalsIgnoreCase(pos) && !(staff instanceof Nurse)) {
            toSave = new Nurse(staff.getStaffId(), staff.getName(), staff.getContactNumber(),
                    staff.getDepartment(), staff.getSalary(), "General Ward", "Day");
        } else if ("Receptionist".equalsIgnoreCase(pos) && !(staff instanceof Receptionist)) {
            toSave = new Receptionist(staff.getStaffId(), staff.getName(), staff.getContactNumber(),
                    staff.getDepartment(), staff.getSalary(), "Desk 01", "English, Sinhala");
        } else if ("Doctor".equalsIgnoreCase(pos) && !(staff instanceof Doctor)) {
            toSave = new GeneralDoctor(staff.getStaffId(), staff.getName(), staff.getDepartment(),
                    staff.getContactNumber(), "Available", 2500.0, "Consulting Room");
        }

        return staffRepository.save(toSave);
    }

    // ==========================================
    // CRUD - Read (Get all staff)
    // ==========================================
    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    // ==========================================
    // CRUD - Read (Get staff by ID)
    // ==========================================
    public Staff getStaffById(String staffId) {
        return staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with ID: " + staffId));
    }

    // ==========================================
    // Search - Find staff by keyword
    // ==========================================
    public List<Staff> searchStaff(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllStaff();
        }
        String lower = keyword.toLowerCase().trim();
        return getAllStaff().stream()
                .filter(s -> s.getStaffId().toLowerCase().contains(lower)
                        || s.getName().toLowerCase().contains(lower)
                        || s.getPosition().toLowerCase().contains(lower)
                        || s.getDepartment().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    // ==========================================
    // CRUD - Update (Update staff details)
    // ==========================================
    public Staff updateStaff(String staffId, Staff updated) {
        Staff existing = getStaffById(staffId);

        if (updated.getName() != null && !updated.getName().isBlank()) {
            existing.setName(updated.getName().trim());
        }
        if (updated.getContactNumber() != null && !updated.getContactNumber().isBlank()) {
            existing.setContactNumber(updated.getContactNumber().trim());
        }
        if (updated.getPosition() != null && !updated.getPosition().isBlank()) {
            existing.setPosition(updated.getPosition().trim());
        }
        if (updated.getDepartment() != null && !updated.getDepartment().isBlank()) {
            existing.setDepartment(updated.getDepartment().trim());
        }
        if (updated.getSalary() > 0) {
            existing.setSalary(updated.getSalary());
        }

        return staffRepository.update(existing);
    }

    // ==========================================
    // CRUD - Delete (Delete staff member)
    // ==========================================
    public boolean deleteStaff(String staffId) {
        getStaffById(staffId);
        return staffRepository.deleteById(staffId);
    }

    private void validateStaff(Staff staff) {
        if (staff.getName() == null || staff.getName().isBlank()) {
            throw new ValidationException("Staff name is required.");
        }
        if (staff.getContactNumber() == null || staff.getContactNumber().isBlank()) {
            throw new ValidationException("Contact number is required.");
        }
        if (staff.getPosition() == null || staff.getPosition().isBlank()) {
            throw new ValidationException("Staff position is required.");
        }
        if (staff.getSalary() <= 0) {
            throw new ValidationException("Salary must be positive.");
        }
    }
}
