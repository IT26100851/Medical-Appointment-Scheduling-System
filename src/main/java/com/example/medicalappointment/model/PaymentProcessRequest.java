package com.example.medicalappointment.model;

public class PaymentProcessRequest {
    private String appointmentId;
    private String patientId;
    private double amount;
    private String method; // "CASH", "CARD", "ONLINE"
    private String detail; // card number or gateway name

    public PaymentProcessRequest() {
    }

    public PaymentProcessRequest(String appointmentId, String patientId, double amount, String method, String detail) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.amount = amount;
        this.method = method;
        this.detail = detail;
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

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
