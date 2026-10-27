package com.example.medicalappointment.controller;

import com.example.medicalappointment.model.ApiResponse;
import com.example.medicalappointment.model.Payment;
import com.example.medicalappointment.model.PaymentProcessRequest;
import com.example.medicalappointment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PaymentController
 * REST endpoints for Medical Billing and Payment Management
 * Demonstrates CRUD operations and payment simulation across Cash, Card, and Online channels
 */
@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // CRUD - Create / Process Payment
    @PostMapping("/process")
    public ResponseEntity<ApiResponse<Payment>> processPayment(@RequestBody PaymentProcessRequest request) {
        Payment processed = paymentService.processPayment(
                request.getAppointmentId(),
                request.getPatientId(),
                request.getAmount(),
                request.getMethod(),
                request.getDetail()
        );
        return ResponseEntity.ok(ApiResponse.ok("Payment processed successfully!", processed));
    }

    // CRUD - Read (Get all payments or search)
    @GetMapping
    public ResponseEntity<ApiResponse<List<Payment>>> getAllPayments(
            @RequestParam(required = false) String search) {
        List<Payment> list = (search != null && !search.isBlank())
                ? paymentService.searchPayments(search)
                : paymentService.getAllPayments();
        return ResponseEntity.ok(ApiResponse.ok("Payments retrieved successfully", list));
    }

    // CRUD - Read (Get payment by ID)
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<Payment>> getPaymentById(@PathVariable String paymentId) {
        Payment payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(ApiResponse.ok("Payment found", payment));
    }

    // Query - Get payment history by Patient ID
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<Payment>>> getPaymentsByPatient(@PathVariable String patientId) {
        List<Payment> list = paymentService.getPaymentsByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.ok("Patient payment history retrieved", list));
    }

    // CRUD - Update (Update payment status)
    @PutMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<Payment>> updatePayment(
            @PathVariable String paymentId,
            @RequestBody Payment payment) {
        Payment updated = paymentService.updatePayment(paymentId, payment);
        return ResponseEntity.ok(ApiResponse.ok("Payment record updated successfully!", updated));
    }

    // CRUD - Delete (Delete payment record)
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<Void>> deletePayment(@PathVariable String paymentId) {
        paymentService.deletePayment(paymentId);
        return ResponseEntity.ok(ApiResponse.ok("Payment record deleted successfully"));
    }
}
