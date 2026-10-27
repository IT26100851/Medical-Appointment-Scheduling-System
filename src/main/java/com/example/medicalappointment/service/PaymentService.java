package com.example.medicalappointment.service;

import com.example.medicalappointment.exception.ResourceNotFoundException;
import com.example.medicalappointment.exception.ValidationException;
import com.example.medicalappointment.model.Appointment;
import com.example.medicalappointment.model.CardPayment;
import com.example.medicalappointment.model.CashPayment;
import com.example.medicalappointment.model.OnlinePayment;
import com.example.medicalappointment.model.Patient;
import com.example.medicalappointment.model.Payment;
import com.example.medicalappointment.repository.AppointmentRepository;
import com.example.medicalappointment.repository.PatientRepository;
import com.example.medicalappointment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * PaymentService
 * Demonstrates:
 * - Simulated medical billing and checkout
 * - Polymorphism: Handles CashPayment, CardPayment, and OnlinePayment
 * - Integrates patient discount calculation (Polymorphism in Patient hierarchy)
 * - CRUD operations for Payment entities stored in data/payments.txt
 */
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          AppointmentRepository appointmentRepository,
                          PatientRepository patientRepository) {
        this.paymentRepository = paymentRepository;
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
    }

    // ==========================================
    // CRUD - Create / Process Payment
    // ==========================================
    public Payment processPayment(String appointmentId, String patientId, double amount,
                                  String method, String cardOrGatewayDetail) {
        if (amount <= 0) {
            throw new ValidationException("Payment amount must be greater than zero.");
        }

        // Verify appointment exists
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        // Verify patient exists
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        // Apply Polymorphic Patient Discount if applicable
        double discount = patient.calculateDiscount(amount);
        double finalAmount = Math.max(0.0, amount - discount);

        String paymentId = "PAY-" + (100 + paymentRepository.findAll().size() + 1);
        String today = LocalDate.now().toString();
        String txnRef = "TXN-" + System.currentTimeMillis() % 100000;

        Payment payment;
        String m = (method != null) ? method.toUpperCase() : "CASH";

        // Polymorphism - Instantiate subclass corresponding to chosen payment channel
        switch (m) {
            case "CARD":
                String last4 = (cardOrGatewayDetail != null && cardOrGatewayDetail.length() >= 4)
                        ? cardOrGatewayDetail.substring(cardOrGatewayDetail.length() - 4)
                        : "8899";
                payment = new CardPayment(paymentId, appointmentId, patientId, finalAmount,
                        today, "PAID", txnRef, "Visa", last4, "AUTH-" + (int)(Math.random() * 9000 + 1000));
                break;

            case "ONLINE":
                String gateway = (cardOrGatewayDetail != null && !cardOrGatewayDetail.isBlank())
                        ? cardOrGatewayDetail
                        : "SecurePay Gateway";
                payment = new OnlinePayment(paymentId, appointmentId, patientId, finalAmount,
                        today, "PAID", txnRef, gateway, "GW-" + UUID.randomUUID().toString().substring(0, 8));
                break;

            case "CASH":
            default:
                payment = new CashPayment(paymentId, appointmentId, patientId, finalAmount,
                        today, "PAID", txnRef, "Front Desk Cashier", finalAmount, 0.0);
                break;
        }

        // Mark appointment as Completed if currently scheduled
        if ("Scheduled".equalsIgnoreCase(appointment.getStatus())) {
            appointment.setStatus("Completed");
            appointmentRepository.update(appointment);
        }

        return paymentRepository.save(payment);
    }

    // ==========================================
    // CRUD - Read (Get all payments)
    // ==========================================
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // ==========================================
    // CRUD - Read (Get payment by ID)
    // ==========================================
    public Payment getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));
    }

    // ==========================================
    // Query - Get payments by Patient ID
    // ==========================================
    public List<Payment> getPaymentsByPatient(String patientId) {
        return paymentRepository.findByPatientId(patientId);
    }

    // ==========================================
    // Search - Find payments by Keyword
    // ==========================================
    public List<Payment> searchPayments(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllPayments();
        }
        String lower = keyword.toLowerCase().trim();
        return getAllPayments().stream()
                .filter(p -> p.getPaymentId().toLowerCase().contains(lower)
                        || p.getPatientId().toLowerCase().contains(lower)
                        || p.getAppointmentId().toLowerCase().contains(lower)
                        || p.getPaymentMethod().toLowerCase().contains(lower)
                        || p.getPaymentStatus().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    // ==========================================
    // CRUD - Update (Update payment status / details)
    // ==========================================
    public Payment updatePayment(String paymentId, Payment updated) {
        Payment existing = getPaymentById(paymentId);
        if (updated.getPaymentStatus() != null && !updated.getPaymentStatus().isBlank()) {
            existing.setPaymentStatus(updated.getPaymentStatus());
        }
        return paymentRepository.update(existing);
    }

    // ==========================================
    // CRUD - Delete (Delete payment record)
    // ==========================================
    public boolean deletePayment(String paymentId) {
        getPaymentById(paymentId);
        return paymentRepository.deleteById(paymentId);
    }
}
