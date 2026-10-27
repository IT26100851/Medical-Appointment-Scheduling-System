package com.example.medicalappointment.controller;

import com.example.medicalappointment.model.ApiResponse;
import com.example.medicalappointment.model.Appointment;
import com.example.medicalappointment.service.AppointmentService;
import com.example.medicalappointment.service.DoctorAvailabilityChecker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AppointmentController
 * REST endpoints for Appointment Management
 * Demonstrates CRUD operations, conflict checking, and status transitions
 */
@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorAvailabilityChecker availabilityChecker;

    public AppointmentController(AppointmentService appointmentService,
                                 DoctorAvailabilityChecker availabilityChecker) {
        this.appointmentService = appointmentService;
        this.availabilityChecker = availabilityChecker;
    }

    // CRUD - Create / Book Appointment
    @PostMapping("/book")
    public ResponseEntity<ApiResponse<Appointment>> bookAppointment(@RequestBody Appointment appointment) {
        Appointment booked = appointmentService.bookAppointment(appointment);
        return ResponseEntity.ok(ApiResponse.ok("Appointment booked successfully!", booked));
    }

    // Abstraction - Check Doctor Availability
    @GetMapping("/check-availability")
    public ResponseEntity<ApiResponse<Boolean>> checkAvailability(
            @RequestParam String doctorId,
            @RequestParam String date,
            @RequestParam String time) {
        boolean available = availabilityChecker.isDoctorAvailable(doctorId, date, time);
        return ResponseEntity.ok(ApiResponse.ok("Availability checked", available));
    }

    // CRUD - Read (Get all appointments or search)
    @GetMapping
    public ResponseEntity<ApiResponse<List<Appointment>>> getAllAppointments(
            @RequestParam(required = false) String search) {
        List<Appointment> appointments = (search != null && !search.isBlank())
                ? appointmentService.searchAppointments(search)
                : appointmentService.getAllAppointments();
        return ResponseEntity.ok(ApiResponse.ok("Appointments retrieved successfully", appointments));
    }

    // CRUD - Read (Get appointment by ID)
    @GetMapping("/{appointmentId}")
    public ResponseEntity<ApiResponse<Appointment>> getAppointmentById(@PathVariable String appointmentId) {
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        return ResponseEntity.ok(ApiResponse.ok("Appointment found", appointment));
    }

    // Query - Get appointments by Patient ID
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<Appointment>>> getAppointmentsByPatient(@PathVariable String patientId) {
        List<Appointment> list = appointmentService.getAppointmentsByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.ok("Patient appointments retrieved", list));
    }

    // Query - Get appointments by Doctor ID
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<ApiResponse<List<Appointment>>> getAppointmentsByDoctor(@PathVariable String doctorId) {
        List<Appointment> list = appointmentService.getAppointmentsByDoctor(doctorId);
        return ResponseEntity.ok(ApiResponse.ok("Doctor appointments retrieved", list));
    }

    // CRUD - Update (Update appointment date/time/reason)
    @PutMapping("/{appointmentId}")
    public ResponseEntity<ApiResponse<Appointment>> updateAppointment(
            @PathVariable String appointmentId,
            @RequestBody Appointment appointment) {
        Appointment updated = appointmentService.updateAppointment(appointmentId, appointment);
        return ResponseEntity.ok(ApiResponse.ok("Appointment updated successfully!", updated));
    }

    // Action - Cancel Appointment
    @PutMapping("/{appointmentId}/cancel")
    public ResponseEntity<ApiResponse<Appointment>> cancelAppointment(@PathVariable String appointmentId) {
        Appointment cancelled = appointmentService.cancelAppointment(appointmentId);
        return ResponseEntity.ok(ApiResponse.ok("Appointment cancelled successfully", cancelled));
    }

    // Action - Complete Appointment
    @PutMapping("/{appointmentId}/complete")
    public ResponseEntity<ApiResponse<Appointment>> completeAppointment(@PathVariable String appointmentId) {
        Appointment completed = appointmentService.completeAppointment(appointmentId);
        return ResponseEntity.ok(ApiResponse.ok("Appointment marked as completed", completed));
    }

    // CRUD - Delete (Delete appointment record)
    @DeleteMapping("/{appointmentId}")
    public ResponseEntity<ApiResponse<Void>> deleteAppointment(@PathVariable String appointmentId) {
        appointmentService.deleteAppointment(appointmentId);
        return ResponseEntity.ok(ApiResponse.ok("Appointment deleted successfully"));
    }
}
