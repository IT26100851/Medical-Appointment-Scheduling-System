package com.example.medicalappointment.repository;

import com.example.medicalappointment.model.CardPayment;
import com.example.medicalappointment.model.CashPayment;
import com.example.medicalappointment.model.OnlinePayment;
import com.example.medicalappointment.model.Payment;
import com.example.medicalappointment.util.FileHandler;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * PaymentRepository
 * Demonstrates:
 * - CRUD operations for Payment entity using File I/O
 * - Polymorphic parsing of CashPayment, CardPayment, and OnlinePayment
 * - Querying payment history by patient and appointment
 * - File read/write against 'data/payments.txt'
 */
@Repository
public class PaymentRepository {

    private static final String FILE_PATH = "data/payments.txt";

    // ==========================================
    // CRUD - Read (Find all payments)
    // ==========================================
    public List<Payment> findAll() {
        List<String> lines = FileHandler.readAllLines(FILE_PATH);
        List<Payment> payments = new ArrayList<>();

        for (String line : lines) {
            try {
                String[] parts = FileHandler.splitLine(line);
                if (parts.length >= 8) {
                    String paymentId = parts[0].trim();
                    String appointmentId = parts[1].trim();
                    String patientId = parts[2].trim();
                    double amount = Double.parseDouble(parts[3].trim());
                    String paymentDate = parts[4].trim();
                    String paymentStatus = parts[5].trim();
                    String paymentMethod = parts[6].trim();
                    String transactionRef = parts[7].trim();
                    String extra = (parts.length > 8) ? parts[8].trim() : "";

                    // Polymorphism & Inheritance - Instantiate specific payment subtype
                    if ("CASH".equalsIgnoreCase(paymentMethod)) {
                        String cashier = "Counter Cashier";
                        double tendered = amount;
                        double change = 0.0;
                        if (!extra.isEmpty() && extra.contains(";")) {
                            for (String token : extra.split(";")) {
                                if (token.startsWith("Cashier:")) cashier = token.substring(8);
                                else if (token.startsWith("Tendered:")) {
                                    try { tendered = Double.parseDouble(token.substring(9)); } catch (Exception ignored) {}
                                } else if (token.startsWith("Change:")) {
                                    try { change = Double.parseDouble(token.substring(7)); } catch (Exception ignored) {}
                                }
                            }
                        }
                        payments.add(new CashPayment(paymentId, appointmentId, patientId, amount,
                                paymentDate, paymentStatus, transactionRef, cashier, tendered, change));
                    } else if ("CARD".equalsIgnoreCase(paymentMethod)) {
                        String cardType = "Visa";
                        String last4 = "4242";
                        String auth = "AUTH-OK";
                        if (!extra.isEmpty() && extra.contains(";")) {
                            for (String token : extra.split(";")) {
                                if (token.startsWith("CardType:")) cardType = token.substring(9);
                                else if (token.startsWith("Last4:")) last4 = token.substring(6);
                                else if (token.startsWith("Auth:")) auth = token.substring(5);
                            }
                        }
                        payments.add(new CardPayment(paymentId, appointmentId, patientId, amount,
                                paymentDate, paymentStatus, transactionRef, cardType, last4, auth));
                    } else { // ONLINE
                        String gateway = "Online Portal";
                        String gtwId = transactionRef;
                        if (!extra.isEmpty() && extra.contains(";")) {
                            for (String token : extra.split(";")) {
                                if (token.startsWith("Gateway:")) gateway = token.substring(8);
                                else if (token.startsWith("GatewayId:")) gtwId = token.substring(10);
                            }
                        }
                        payments.add(new OnlinePayment(paymentId, appointmentId, patientId, amount,
                                paymentDate, paymentStatus, transactionRef, gateway, gtwId));
                    }
                }
            } catch (Exception ex) {
                System.err.println("[PaymentRepository] Skipping invalid record: " + line + " - " + ex.getMessage());
            }
        }
        return payments;
    }

    // ==========================================
    // CRUD - Read (Find payment by ID)
    // ==========================================
    public Optional<Payment> findById(String paymentId) {
        if (paymentId == null) return Optional.empty();
        return findAll().stream()
                .filter(p -> paymentId.equalsIgnoreCase(p.getPaymentId()))
                .findFirst();
    }

    // ==========================================
    // Query - Find payment history by Patient ID
    // ==========================================
    public List<Payment> findByPatientId(String patientId) {
        if (patientId == null) return new ArrayList<>();
        return findAll().stream()
                .filter(p -> patientId.equalsIgnoreCase(p.getPatientId()))
                .collect(Collectors.toList());
    }

    // ==========================================
    // Query - Find payment by Appointment ID
    // ==========================================
    public Optional<Payment> findByAppointmentId(String appointmentId) {
        if (appointmentId == null) return Optional.empty();
        return findAll().stream()
                .filter(p -> appointmentId.equalsIgnoreCase(p.getAppointmentId()))
                .findFirst();
    }

    // ==========================================
    // CRUD - Create (Save new payment)
    // ==========================================
    public synchronized Payment save(Payment payment) {
        List<Payment> existing = findAll();
        boolean exists = existing.stream().anyMatch(p -> p.getPaymentId().equalsIgnoreCase(payment.getPaymentId()));
        if (exists) {
            return update(payment);
        }

        FileHandler.appendLine(FILE_PATH, payment.toFileString());
        return payment;
    }

    // ==========================================
    // CRUD - Update (Update existing payment)
    // ==========================================
    public synchronized Payment update(Payment updatedPayment) {
        List<Payment> all = findAll();
        List<String> newLines = new ArrayList<>();
        boolean found = false;

        for (Payment p : all) {
            if (p.getPaymentId().equalsIgnoreCase(updatedPayment.getPaymentId())) {
                newLines.add(updatedPayment.toFileString());
                found = true;
            } else {
                newLines.add(p.toFileString());
            }
        }

        if (found) {
            FileHandler.writeAllLines(FILE_PATH, newLines);
            return updatedPayment;
        } else {
            return save(updatedPayment);
        }
    }

    // ==========================================
    // CRUD - Delete (Delete payment by ID)
    // ==========================================
    public synchronized boolean deleteById(String paymentId) {
        List<Payment> all = findAll();
        List<String> remainingLines = new ArrayList<>();
        boolean removed = false;

        for (Payment p : all) {
            if (p.getPaymentId().equalsIgnoreCase(paymentId)) {
                removed = true;
            } else {
                remainingLines.add(p.toFileString());
            }
        }

        if (removed) {
            FileHandler.writeAllLines(FILE_PATH, remainingLines);
        }
        return removed;
    }
}
