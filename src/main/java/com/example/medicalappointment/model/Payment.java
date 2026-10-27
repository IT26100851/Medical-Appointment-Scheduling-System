package com.example.medicalappointment.model;

/**
 * Abstract Payment class demonstrating:
 * - Abstraction: Outlines the contract for all payment methods in the hospital.
 * - Encapsulation: Private financial and audit variables.
 * - Inheritance: Subclassed by CashPayment, CardPayment, and OnlinePayment.
 * - Polymorphism: Abstract methods calculateProcessingFee and getPaymentReceiptDetails.
 */
public abstract class Payment {

    // Encapsulation - Private fields
    private String paymentId;
    private String appointmentId;
    private String patientId;
    private double amount;
    private String paymentDate;   // YYYY-MM-DD
    private String paymentStatus; // "PAID", "PENDING", "REFUNDED"
    private String paymentMethod; // "CASH", "CARD", "ONLINE"
    private String transactionReference;

    public Payment() {
        this.paymentStatus = "PAID";
    }

    public Payment(String paymentId, String appointmentId, String patientId, double amount,
                   String paymentDate, String paymentStatus, String paymentMethod, String transactionReference) {
        this.paymentId = paymentId;
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentStatus = (paymentStatus == null || paymentStatus.isBlank()) ? "PAID" : paymentStatus;
        this.paymentMethod = paymentMethod;
        this.transactionReference = transactionReference;
    }

    // Abstraction & Polymorphism - Calculated differently across payment channels
    public abstract double calculateProcessingFee();

    // Abstraction & Polymorphism - Formatted receipt details
    public abstract String getPaymentReceiptDetails();

    // File Handling serialization for payments.txt
    public String toFileString() {
        return String.join("|",
                safe(paymentId),
                safe(appointmentId),
                safe(patientId),
                String.format("%.2f", amount),
                safe(paymentDate),
                safe(paymentStatus),
                safe(paymentMethod),
                safe(transactionReference),
                safe(getPaymentSpecificData())
        );
    }

    protected abstract String getPaymentSpecificData();

    protected String safe(String val) {
        return (val == null) ? "" : val.replace("|", " ").trim();
    }

    // Getters and Setters
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }
}
