package com.example.medicalappointment.controller;

import com.example.medicalappointment.model.ApiResponse;
import com.example.medicalappointment.model.LoginRequest;
import com.example.medicalappointment.model.UserSession;
import com.example.medicalappointment.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController
 * Handles simple login and authentication for Patient, Admin, and Staff roles
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserSession>> login(@RequestBody LoginRequest request) {
        UserSession session = authService.authenticate(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful!", session));
    }
}
