package com.example.medicalappointment.service;

import com.example.medicalappointment.exception.ResourceNotFoundException;
import com.example.medicalappointment.exception.ValidationException;
import com.example.medicalappointment.model.Appointment;
import com.example.medicalappointment.model.EmergencyAppointment;
import com.example.medicalappointment.model.FollowUpAppointment;
import com.example.medicalappointment.repository.AppointmentRepository;
import com.example.medicalappointment.repository.DoctorRepository;
import com.example.medicalappointment.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AppointmentService
 * Demonstrates:
 * - Abstraction: Relies on DoctorAvailabilityChecker interface to validate booking availability.
 * - Business Rules: Prevent conflicting bookings, ensure patient and doctor exist.
 * - CRUD operations for Appointment management.
 * - Polymorphism: Handles Standard, Emergency, and FollowUp appointments.
 */
@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    // Abstraction - Uses interface rather than direct coupling
    private final DoctorAvailabilityChecker availabilityChecker;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              PatientRepository patientRepository,
                              DoctorAvailabilityChecker availabilityChecker) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.availabilityChecker = availabilityChecker;
    }

    // ==========================================
    // CRUD - Create / Book Appointment
    // ==========================================
    public Appointment bookAppointment(Appointment appointment) {
        validateAppointmentInputs(appointment);

        // Verify patient exists
        if (patientRepository.findById(appointment.getPatientId()).isEmpty()) {
            throw new ResourceNotFoundException("Patient does not exist with ID: " + appointment.getPatientId());
        }

        // Verify doctor exists and is available using Abstraction (DoctorAvailabilityChecker)
        availabilityChecker.validateDoctorAvailability(
                appointment.getDoctorId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                null
        );

        // Generate unique appointment ID
        if (appointment.getAppointmentId() == null || appointment.getAppointmentId().isBlank()) {
            appointment.setAppointmentId("APT-" + (100 + appointmentRepository.findAll().size() + 1));
        }

        appointment.setStatus("Scheduled");

        // Polymorphic appointment creation
        Appointment toSave = appointment;
        if ("Emergency".equalsIgnoreCase(appointment.getAppointmentType()) && !(appointment instanceof EmergencyAppointment)) {
            toSave = new EmergencyAppointment(appointment.getAppointmentId(), appointment.getPatientId(),
                    appointment.getDoctorId(), appointment.getAppointmentDate(), appointment.getAppointmentTime(),
                    "Scheduled", appointment.getReason(), "High Priority");
        } else if ("FollowUp".equalsIgnoreCase(appointment.getAppointmentType()) && !(appointment instanceof FollowUpAppointment)) {
            toSave = new FollowUpAppointment(appointment.getAppointmentId(), appointment.getPatientId(),
                    appointment.getDoctorId(), appointment.getAppointmentDate(), appointment.getAppointmentTime(),
                    "Scheduled", appointment.getReason(), "Prev-" + appointment.getPatientId());
        }

        return appointmentRepository.save(toSave);
    }

    // ==========================================
    // CRUD - Read (Get all appointments)
    // ==========================================
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    // ==========================================
    // CRUD - Read (Get appointment by ID)
    // ==========================================
    public Appointment getAppointmentById(String appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));
    }

    // ==========================================
    // Query - Get appointments by Patient ID
    // ==========================================
    public List<Appointment> getAppointmentsByPatient(String patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    // ==========================================
    // Query - Get appointments by Doctor ID
    // ==========================================
    public List<Appointment> getAppointmentsByDoctor(String doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    // ==========================================
    // Search - Find appointments by keyword
    // ==========================================
    public List<Appointment> searchAppointments(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllAppointments();
        }
        String lower = keyword.toLowerCase().trim();
        return getAllAppointments().stream()
                .filter(a -> a.getAppointmentId().toLowerCase().contains(lower)
                        || a.getPatientId().toLowerCase().contains(lower)
                        || a.getDoctorId().toLowerCase().contains(lower)
                        || a.getStatus().toLowerCase().contains(lower)
                        || (a.getReason() != null && a.getReason().toLowerCase().contains(lower)))
                .collect(Collectors.toList());
    }

    // ==========================================
    // CRUD - Update (Update appointment date, time, or reason)
    // ==========================================
    public Appointment updateAppointment(String appointmentId, Appointment updatedData) {
        Appointment existing = getAppointmentById(appointmentId);

        // If date or time changed, re-validate availability
        boolean timeChanged = !existing.getAppointmentDate().equals(updatedData.getAppointmentDate())
                || !existing.getAppointmentTime().equals(updatedData.getAppointmentTime());

        if (timeChanged) {
            if (updatedData.getAppointmentDate() != null && !updatedData.getAppointmentDate().isBlank()) {
                try {
                    LocalDate newDate = LocalDate.parse(updatedData.getAppointmentDate().trim());
                    if (newDate.isBefore(LocalDate.now())) {
                        throw new ValidationException("Appointment date cannot be in the past. Please select today or a future date.");
                    }
                } catch (Exception e) {
                    if (e instanceof ValidationException) throw e;
                    throw new ValidationException("Invalid appointment date format. Use YYYY-MM-DD.");
                }
            }
            availabilityChecker.validateDoctorAvailability(
                    existing.getDoctorId(),
                    updatedData.getAppointmentDate(),
                    updatedData.getAppointmentTime(),
                    appointmentId
            );
            existing.setAppointmentDate(updatedData.getAppointmentDate());
            existing.setAppointmentTime(updatedData.getAppointmentTime());
        }

        if (updatedData.getReason() != null && !updatedData.getReason().isBlank()) {
            existing.setReason(updatedData.getReason().trim());
        }
        if (updatedData.getStatus() != null && !updatedData.getStatus().isBlank()) {
            existing.setStatus(updatedData.getStatus().trim());
        }

        return appointmentRepository.update(existing);
    }

    // ==========================================
    // Business Action - Cancel Appointment
    // ==========================================
    public Appointment cancelAppointment(String appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        if (!appointment.canBeCancelled()) {
            throw new ValidationException("Appointment is already " + appointment.getStatus() + " and cannot be cancelled.");
        }
        appointment.setStatus("Cancelled");
        return appointmentRepository.update(appointment);
    }

    // ==========================================
    // Business Action - Complete Appointment
    // ==========================================
    public Appointment completeAppointment(String appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.setStatus("Completed");
        return appointmentRepository.update(appointment);
    }

    // ==========================================
    // CRUD - Delete (Delete appointment record)
    // ==========================================
    public boolean deleteAppointment(String appointmentId) {
        getAppointmentById(appointmentId);
        return appointmentRepository.deleteById(appointmentId);
    }

    private void validateAppointmentInputs(Appointment appointment) {
        if (appointment.getPatientId() == null || appointment.getPatientId().isBlank()) {
            throw new ValidationException("Patient ID is required.");
        }
        if (appointment.getDoctorId() == null || appointment.getDoctorId().isBlank()) {
            throw new ValidationException("Doctor ID is required.");
        }
        if (appointment.getAppointmentDate() == null || appointment.getAppointmentDate().isBlank()) {
            throw new ValidationException("Appointment date is required.");
        }
        try {
            LocalDate date = LocalDate.parse(appointment.getAppointmentDate().trim());
            if (date.isBefore(LocalDate.now())) {
                throw new ValidationException("Appointment date cannot be in the past. Please select today or a future date.");
            }
        } catch (Exception e) {
            if (e instanceof ValidationException) throw e;
            throw new ValidationException("Invalid appointment date format. Use YYYY-MM-DD.");
        }
        if (appointment.getAppointmentTime() == null || appointment.getAppointmentTime().isBlank()) {
            throw new ValidationException("Appointment time is required.");
        }
        if (appointment.getReason() == null || appointment.getReason().isBlank()) {
            throw new ValidationException("Reason for visit is required.");
        }
    }
}
