package com.example.medicalappointment.service;

import com.example.medicalappointment.exception.ResourceNotFoundException;
import com.example.medicalappointment.exception.ValidationException;
import com.example.medicalappointment.model.Doctor;
import com.example.medicalappointment.model.GeneralDoctor;
import com.example.medicalappointment.model.SpecialistDoctor;
import com.example.medicalappointment.model.Surgeon;
import com.example.medicalappointment.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DoctorService
 * Demonstrates:
 * - CRUD operations for Doctor management
 * - Specialized queries (by specialization, availability)
 * - Polymorphism: instantiation and fee calculation across GeneralDoctor, SpecialistDoctor, and Surgeon
 */
@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    // ==========================================
    // CRUD - Create (Add new doctor)
    // ==========================================
    public Doctor addDoctor(Doctor doctor) {
        validateDoctorData(doctor);

        if (doctor.getDoctorId() == null || doctor.getDoctorId().isBlank()) {
            doctor.setDoctorId("DOC-" + (100 + doctorRepository.findAll().size() + 1));
        }

        // Polymorphic instantiation based on doctorType
        Doctor toSave = doctor;
        String type = doctor.getDoctorType();
        if ("SpecialistDoctor".equalsIgnoreCase(type) && !(doctor instanceof SpecialistDoctor)) {
            toSave = new SpecialistDoctor(doctor.getDoctorId(), doctor.getName(), doctor.getSpecialization(),
                    doctor.getContactNumber(), doctor.getAvailabilityStatus(), doctor.getConsultationFee(),
                    "Board Certified Specialist");
        } else if ("Surgeon".equalsIgnoreCase(type) && !(doctor instanceof Surgeon)) {
            toSave = new Surgeon(doctor.getDoctorId(), doctor.getName(), doctor.getSpecialization(),
                    doctor.getContactNumber(), doctor.getAvailabilityStatus(), doctor.getConsultationFee(),
                    "OT-Main");
        } else if (!(doctor instanceof GeneralDoctor) && !(doctor instanceof SpecialistDoctor) && !(doctor instanceof Surgeon)) {
            toSave = new GeneralDoctor(doctor.getDoctorId(), doctor.getName(), doctor.getSpecialization(),
                    doctor.getContactNumber(), doctor.getAvailabilityStatus(), doctor.getConsultationFee(),
                    "Room " + (100 + doctorRepository.findAll().size()));
        }

        return doctorRepository.save(toSave);
    }

    // ==========================================
    // CRUD - Read (Get all doctors)
    // ==========================================
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    // ==========================================
    // CRUD - Read (Get doctor by ID)
    // ==========================================
    public Doctor getDoctorById(String doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));
    }

    // ==========================================
    // Search - Find doctors by Keyword (Name / Specialization)
    // ==========================================
    public List<Doctor> searchDoctors(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllDoctors();
        }
        String lower = keyword.toLowerCase().trim();
        return getAllDoctors().stream()
                .filter(d -> d.getDoctorId().toLowerCase().contains(lower)
                        || d.getName().toLowerCase().contains(lower)
                        || d.getSpecialization().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    // ==========================================
    // Search - Find by Specialization
    // ==========================================
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }

    // ==========================================
    // Search - Find by Availability Status
    // ==========================================
    public List<Doctor> getDoctorsByAvailability(String status) {
        return doctorRepository.findByAvailabilityStatus(status);
    }

    // ==========================================
    // CRUD - Update (Update existing doctor)
    // ==========================================
    public Doctor updateDoctor(String doctorId, Doctor updatedDoctor) {
        Doctor existing = getDoctorById(doctorId);

        if (updatedDoctor.getName() != null && !updatedDoctor.getName().isBlank()) {
            existing.setName(updatedDoctor.getName().trim());
        }
        if (updatedDoctor.getSpecialization() != null && !updatedDoctor.getSpecialization().isBlank()) {
            existing.setSpecialization(updatedDoctor.getSpecialization().trim());
        }
        if (updatedDoctor.getContactNumber() != null && !updatedDoctor.getContactNumber().isBlank()) {
            existing.setContactNumber(updatedDoctor.getContactNumber().trim());
        }
        if (updatedDoctor.getAvailabilityStatus() != null && !updatedDoctor.getAvailabilityStatus().isBlank()) {
            existing.setAvailabilityStatus(updatedDoctor.getAvailabilityStatus().trim());
        }
        if (updatedDoctor.getConsultationFee() > 0) {
            existing.setConsultationFee(updatedDoctor.getConsultationFee());
        }
        if (updatedDoctor.getDoctorType() != null && !updatedDoctor.getDoctorType().isBlank()) {
            existing.setDoctorType(updatedDoctor.getDoctorType().trim());
        }

        return doctorRepository.update(existing);
    }

    // ==========================================
    // CRUD - Delete (Delete doctor by ID)
    // ==========================================
    public boolean deleteDoctor(String doctorId) {
        getDoctorById(doctorId); // Ensure exists
        return doctorRepository.deleteById(doctorId);
    }

    private void validateDoctorData(Doctor doctor) {
        if (doctor.getName() == null || doctor.getName().trim().isEmpty()) {
            throw new ValidationException("Doctor name is required.");
        }
        if (doctor.getSpecialization() == null || doctor.getSpecialization().trim().isEmpty()) {
            throw new ValidationException("Specialization is required.");
        }
        if (doctor.getContactNumber() == null || doctor.getContactNumber().trim().isEmpty()) {
            throw new ValidationException("Contact number is required.");
        }
        if (doctor.getConsultationFee() <= 0) {
            throw new ValidationException("Consultation fee must be greater than 0.");
        }
    }
}
