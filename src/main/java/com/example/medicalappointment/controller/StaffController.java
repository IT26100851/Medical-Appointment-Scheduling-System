package com.example.medicalappointment.controller;

import com.example.medicalappointment.model.ApiResponse;
import com.example.medicalappointment.model.GeneralDoctor;
import com.example.medicalappointment.model.Nurse;
import com.example.medicalappointment.model.Receptionist;
import com.example.medicalappointment.model.Staff;
import com.example.medicalappointment.service.StaffService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * StaffController
 * REST endpoints for Hospital Staff Management
 * Demonstrates CRUD operations and polymorphic staff handling (Doctor, Nurse, Receptionist)
 */
@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "*")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    // CRUD - Create (Add staff member)
    @PostMapping
    public ResponseEntity<ApiResponse<Staff>> addStaff(@RequestBody Map<String, Object> body) {
        String staffId = (String) body.get("staffId");
        String name = (String) body.get("name");
        String contactNumber = (String) body.get("contactNumber");
        String position = (String) body.get("position");
        String department = (String) body.get("department");
        double salary = 50000.0;
        if (body.get("salary") != null) {
            salary = Double.parseDouble(body.get("salary").toString());
        }

        Staff staff;
        if ("Nurse".equalsIgnoreCase(position)) {
            String ward = body.getOrDefault("ward", "General Ward").toString();
            String shift = body.getOrDefault("shift", "Day").toString();
            staff = new Nurse(staffId, name, contactNumber, department, salary, ward, shift);
        } else if ("Receptionist".equalsIgnoreCase(position)) {
            String desk = body.getOrDefault("desk", "Desk 01").toString();
            String lang = body.getOrDefault("languages", "English").toString();
            staff = new Receptionist(staffId, name, contactNumber, department, salary, desk, lang);
        } else {
            staff = new GeneralDoctor(staffId, name, department, contactNumber, "Available", 2500.0, "OPD Room");
        }

        Staff saved = staffService.addStaff(staff);
        return ResponseEntity.ok(ApiResponse.ok("Staff member added successfully!", saved));
    }

    // CRUD - Read (Get all staff or search)
    @GetMapping
    public ResponseEntity<ApiResponse<List<Staff>>> getAllStaff(
            @RequestParam(required = false) String search) {
        List<Staff> list = (search != null && !search.isBlank())
                ? staffService.searchStaff(search)
                : staffService.getAllStaff();
        return ResponseEntity.ok(ApiResponse.ok("Staff members retrieved successfully", list));
    }

    // CRUD - Read (Get staff by ID)
    @GetMapping("/{staffId}")
    public ResponseEntity<ApiResponse<Staff>> getStaffById(@PathVariable String staffId) {
        Staff staff = staffService.getStaffById(staffId);
        return ResponseEntity.ok(ApiResponse.ok("Staff member found", staff));
    }

    // CRUD - Update (Update staff details)
    @PutMapping("/{staffId}")
    public ResponseEntity<ApiResponse<Staff>> updateStaff(
            @PathVariable String staffId,
            @RequestBody Map<String, Object> body) {
        Staff existing = staffService.getStaffById(staffId);

        if (body.containsKey("name")) existing.setName(body.get("name").toString());
        if (body.containsKey("contactNumber")) existing.setContactNumber(body.get("contactNumber").toString());
        if (body.containsKey("position")) existing.setPosition(body.get("position").toString());
        if (body.containsKey("department")) existing.setDepartment(body.get("department").toString());
        if (body.containsKey("salary")) existing.setSalary(Double.parseDouble(body.get("salary").toString()));

        Staff updated = staffService.updateStaff(staffId, existing);
        return ResponseEntity.ok(ApiResponse.ok("Staff details updated successfully!", updated));
    }

    // CRUD - Delete (Delete staff member)
    @DeleteMapping("/{staffId}")
    public ResponseEntity<ApiResponse<Void>> deleteStaff(@PathVariable String staffId) {
        staffService.deleteStaff(staffId);
        return ResponseEntity.ok(ApiResponse.ok("Staff member deleted successfully"));
    }
}
