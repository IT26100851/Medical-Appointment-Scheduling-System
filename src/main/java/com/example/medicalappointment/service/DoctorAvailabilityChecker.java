package com.example.medicalappointment.service;

/**
 * DoctorAvailabilityChecker Interface
 * Demonstrates:
 * - Abstraction: Separates the contract for verifying medical doctor availability
 *   from its concrete scheduling implementation.
 */
public interface DoctorAvailabilityChecker {

    /**
     * Checks if a doctor is generally available and not booked at the given date/time slot.
     *
     * @param doctorId Doctor ID
     * @param date     Date of consultation (YYYY-MM-DD)
     * @param time     Time of consultation (HH:mm)
     * @return true if doctor is free to take the appointment, false otherwise
     */
    boolean isDoctorAvailable(String doctorId, String date, String time);

    /**
     * Validates availability and throws ConflictException or ValidationException if unavailable.
     *
     * @param doctorId             Doctor ID
     * @param date                 Consultation date
     * @param time                 Consultation time
     * @param excludeAppointmentId Optional appointment ID to exclude (during updates)
     */
    void validateDoctorAvailability(String doctorId, String date, String time, String excludeAppointmentId);
}
