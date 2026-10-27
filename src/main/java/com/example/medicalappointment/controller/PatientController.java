package com.example.medicalappointment.controller;

import com.example.medicalappointment.model.ApiResponse;
import com.example.medicalappointment.model.Patient;
import com.example.medicalappointment.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PatientController
 * REST endpoints for Patient Management
 * Demonstrates CRUD operations (Create, Read, Update, Delete)
 */
@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // CRUD - Create (Register Patient)
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Patient>> registerPatient(@RequestBody Patient patient) {
        Patient registered = patientService.registerPatient(patient);
        return ResponseEntity.ok(ApiResponse.ok("Patient registered successfully!", registered));
    }

    // CRUD - Read (Get all patients or search by keyword)
    @GetMapping
    public ResponseEntity<ApiResponse<List<Patient>>> getAllPatients(@RequestParam(required = false) String search) {
        List<Patient> patients = (search != null && !search.isBlank())
                ? patientService.searchPatients(search)
                : patientService.getAllPatients();
        return ResponseEntity.ok(ApiResponse.ok("Patients retrieved successfully", patients));
    }

    // CRUD - Read (Get patient by ID)
    @GetMapping("/{patientId}")
    public ResponseEntity<ApiResponse<Patient>> getPatientById(@PathVariable String patientId) {
        Patient patient = patientService.getPatientById(patientId);
        return ResponseEntity.ok(ApiResponse.ok("Patient found", patient));
    }

    // CRUD - Update (Update patient details)
    @PutMapping("/{patientId}")
    public ResponseEntity<ApiResponse<Patient>> updatePatient(@PathVariable String patientId,
                                                              @RequestBody Patient patient) {
        Patient updated = patientService.updatePatient(patientId, patient);
        return ResponseEntity.ok(ApiResponse.ok("Patient updated successfully!", updated));
    }

    // CRUD - Delete (Delete patient account)
    @DeleteMapping("/{patientId}")
    public ResponseEntity<ApiResponse<Void>> deletePatient(@PathVariable String patientId) {
        boolean deleted = patientService.deletePatient(patientId);
        return ResponseEntity.ok(ApiResponse.ok("Patient account deleted successfully"));
    }
}
