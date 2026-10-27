package com.example.medicalappointment.controller;

import com.example.medicalappointment.model.ApiResponse;
import com.example.medicalappointment.model.Doctor;
import com.example.medicalappointment.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DoctorController
 * REST endpoints for Doctor Management
 * Demonstrates CRUD operations, specialization filter, and availability search
 */
@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    // CRUD - Create (Add new doctor)
    @PostMapping
    public ResponseEntity<ApiResponse<Doctor>> addDoctor(@RequestBody Doctor doctor) {
        Doctor saved = doctorService.addDoctor(doctor);
        return ResponseEntity.ok(ApiResponse.ok("Doctor added successfully!", saved));
    }

    // CRUD - Read (Get all doctors, with optional filters for search, specialization, availability)
    @GetMapping
    public ResponseEntity<ApiResponse<List<Doctor>>> getDoctors(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String availability) {

        List<Doctor> doctors;
        if (specialization != null && !specialization.isBlank()) {
            doctors = doctorService.getDoctorsBySpecialization(specialization);
        } else if (availability != null && !availability.isBlank()) {
            doctors = doctorService.getDoctorsByAvailability(availability);
        } else if (search != null && !search.isBlank()) {
            doctors = doctorService.searchDoctors(search);
        } else {
            doctors = doctorService.getAllDoctors();
        }

        return ResponseEntity.ok(ApiResponse.ok("Doctors retrieved successfully", doctors));
    }

    // CRUD - Read (Get doctor by ID)
    @GetMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<Doctor>> getDoctorById(@PathVariable String doctorId) {
        Doctor doctor = doctorService.getDoctorById(doctorId);
        return ResponseEntity.ok(ApiResponse.ok("Doctor found", doctor));
    }

    // CRUD - Update (Update doctor information)
    @PutMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<Doctor>> updateDoctor(@PathVariable String doctorId,
                                                            @RequestBody Doctor doctor) {
        Doctor updated = doctorService.updateDoctor(doctorId, doctor);
        return ResponseEntity.ok(ApiResponse.ok("Doctor updated successfully!", updated));
    }

    // CRUD - Delete (Delete doctor)
    @DeleteMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<Void>> deleteDoctor(@PathVariable String doctorId) {
        doctorService.deleteDoctor(doctorId);
        return ResponseEntity.ok(ApiResponse.ok("Doctor removed successfully"));
    }
}
