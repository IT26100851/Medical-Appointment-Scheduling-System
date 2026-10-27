package com.example.medicalappointment.service;

import com.example.medicalappointment.exception.ConflictException;
import com.example.medicalappointment.exception.ResourceNotFoundException;
import com.example.medicalappointment.exception.ValidationException;
import com.example.medicalappointment.model.Appointment;
import com.example.medicalappointment.model.Doctor;
import com.example.medicalappointment.repository.AppointmentRepository;
import com.example.medicalappointment.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Concrete implementation of DoctorAvailabilityChecker
 * Demonstrates:
 * - Abstraction: Concrete implementation of interface contract.
 * - Conflict detection logic preventing double-booking of doctors.
 */
@Service
public class DefaultDoctorAvailabilityChecker implements DoctorAvailabilityChecker {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public DefaultDoctorAvailabilityChecker(DoctorRepository doctorRepository,
                                            AppointmentRepository appointmentRepository) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public boolean isDoctorAvailable(String doctorId, String date, String time) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty() || !doctorOpt.get().isAvailable()) {
            return false;
        }

        // Check if there is an existing scheduled appointment at the exact same date and time
        Optional<Appointment> conflict = appointmentRepository.findActiveByDoctorDateTime(doctorId, date, time);
        return conflict.isEmpty();
    }

    @Override
    public void validateDoctorAvailability(String doctorId, String date, String time, String excludeAppointmentId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));

        if (!doctor.isAvailable()) {
            throw new ValidationException("Doctor " + doctor.getName() + " is currently marked as '"
                    + doctor.getAvailabilityStatus() + "' and cannot accept bookings.");
        }

        Optional<Appointment> conflict = appointmentRepository.findActiveByDoctorDateTime(doctorId, date, time);
        if (conflict.isPresent()) {
            Appointment existing = conflict.get();
            // If the conflicting appointment is the one we are currently updating, ignore it
            if (excludeAppointmentId == null || !excludeAppointmentId.equalsIgnoreCase(existing.getAppointmentId())) {
                throw new ConflictException("Doctor " + doctor.getName() + " is already booked for an appointment on "
                        + date + " at " + time + ". Please select a different time slot.");
            }
        }
    }
}
