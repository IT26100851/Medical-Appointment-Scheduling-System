package com.example.medicalappointment.controller;

import com.example.medicalappointment.model.AdminStats;
import com.example.medicalappointment.model.ApiResponse;
import com.example.medicalappointment.model.Appointment;
import com.example.medicalappointment.model.Payment;
import com.example.medicalappointment.model.Review;
import com.example.medicalappointment.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DashboardController
 * Aggregates statistics for Admin Dashboard and Patient Dashboard
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final PaymentService paymentService;
    private final StaffService staffService;
    private final ReviewService reviewService;

    public DashboardController(PatientService patientService,
                               DoctorService doctorService,
                               AppointmentService appointmentService,
                               PaymentService paymentService,
                               StaffService staffService,
                               ReviewService reviewService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.paymentService = paymentService;
        this.staffService = staffService;
        this.reviewService = reviewService;
    }

    // Admin Dashboard Statistics
    @GetMapping("/admin-stats")
    public ResponseEntity<ApiResponse<AdminStats>> getAdminStats() {
        AdminStats stats = new AdminStats();

        stats.setTotalPatients(patientService.getAllPatients().size());
        stats.setTotalDoctors(doctorService.getAllDoctors().size());
        stats.setTotalStaff(staffService.getAllStaff().size());

        List<Appointment> appointments = appointmentService.getAllAppointments();
        stats.setTotalAppointments(appointments.size());
        stats.setPendingAppointments((int) appointments.stream()
                .filter(a -> "Scheduled".equalsIgnoreCase(a.getStatus()) || "In-Progress".equalsIgnoreCase(a.getStatus()))
                .count());
        stats.setCompletedAppointments((int) appointments.stream()
                .filter(a -> "Completed".equalsIgnoreCase(a.getStatus()))
                .count());
        stats.setCancelledAppointments((int) appointments.stream()
                .filter(a -> "Cancelled".equalsIgnoreCase(a.getStatus()))
                .count());

        List<Payment> payments = paymentService.getAllPayments();
        stats.setTotalPayments(payments.size());
        stats.setTotalRevenue(payments.stream()
                .filter(p -> "PAID".equalsIgnoreCase(p.getPaymentStatus()))
                .mapToDouble(Payment::getAmount)
                .sum());

        List<Review> reviews = reviewService.getAllReviews();
        stats.setRecentReviews(reviews.stream().limit(5).collect(Collectors.toList()));

        return ResponseEntity.ok(ApiResponse.ok("Admin statistics loaded", stats));
    }

    // Patient Dashboard Statistics
    @GetMapping("/patient-stats/{patientId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPatientStats(@PathVariable String patientId) {
        Map<String, Object> data = new HashMap<>();

        List<Appointment> allApts = appointmentService.getAppointmentsByPatient(patientId);
        List<Appointment> upcoming = allApts.stream()
                .filter(a -> "Scheduled".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());
        List<Appointment> past = allApts.stream()
                .filter(a -> "Completed".equalsIgnoreCase(a.getStatus()) || "Cancelled".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());

        List<Payment> payments = paymentService.getPaymentsByPatient(patientId);

        data.put("totalAppointments", allApts.size());
        data.put("upcomingAppointments", upcoming);
        data.put("pastAppointments", past);
        data.put("payments", payments);
        data.put("availableDoctorsCount", doctorService.getDoctorsByAvailability("Available").size());

        return ResponseEntity.ok(ApiResponse.ok("Patient statistics loaded", data));
    }

    // Staff Dashboard Operational Statistics (Restricted to clinical ops, no admin revenue)
    @GetMapping("/staff-stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStaffStats() {
        Map<String, Object> data = new HashMap<>();

        List<Appointment> allApts = appointmentService.getAllAppointments();
        long scheduledCount = allApts.stream()
                .filter(a -> "Scheduled".equalsIgnoreCase(a.getStatus()))
                .count();
        long inProgressCount = allApts.stream()
                .filter(a -> "In-Progress".equalsIgnoreCase(a.getStatus()))
                .count();
        long completedCount = allApts.stream()
                .filter(a -> "Completed".equalsIgnoreCase(a.getStatus()))
                .count();

        var doctors = doctorService.getAllDoctors();
        long availableDoctorsCount = doctors.stream()
                .filter(d -> "Available".equalsIgnoreCase(d.getAvailabilityStatus()))
                .count();

        data.put("totalAppointments", allApts.size());
        data.put("scheduledCount", scheduledCount);
        data.put("inProgressCount", inProgressCount);
        data.put("completedCount", completedCount);
        data.put("availableDoctorsCount", availableDoctorsCount);
        data.put("totalDoctors", doctors.size());
        data.put("recentAppointments", allApts);
        data.put("doctors", doctors);

        return ResponseEntity.ok(ApiResponse.ok("Staff operational metrics loaded", data));
    }
}
