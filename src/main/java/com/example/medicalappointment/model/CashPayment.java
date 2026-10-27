package com.example.medicalappointment.model;

/**
 * CashPayment class demonstrating:
 * - Inheritance: Subclass of abstract Payment.
 * - Polymorphism: Implementation of fee calculation and receipt generation.
 */
public class CashPayment extends Payment {

    // Encapsulation - Specific fields
    private String cashierName;
    private double tenderedAmount;
    private double changeGiven;

    public CashPayment() {
        super();
        setPaymentMethod("CASH");
        this.cashierName = "Front Desk Cashier";
    }

    public CashPayment(String paymentId, String appointmentId, String patientId, double amount,
                       String paymentDate, String paymentStatus, String transactionReference,
                       String cashierName, double tenderedAmount, double changeGiven) {
        super(paymentId, appointmentId, patientId, amount, paymentDate, paymentStatus, "CASH", transactionReference);
        this.cashierName = cashierName;
        this.tenderedAmount = tenderedAmount;
        this.changeGiven = changeGiven;
    }

    // Polymorphism - No processing fee for cash transactions
    @Override
    public double calculateProcessingFee() {
        return 0.0;
    }

    // Polymorphism - Overridden receipt generation
    @Override
    public String getPaymentReceiptDetails() {
        return "Cash Payment collected at counter by " + cashierName +
                " [Tendered: $" + String.format("%.2f", tenderedAmount) +
                ", Change: $" + String.format("%.2f", changeGiven) + "]";
    }

    @Override
    protected String getPaymentSpecificData() {
        return "Cashier:" + cashierName + ";Tendered:" + tenderedAmount + ";Change:" + changeGiven;
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    public double getTenderedAmount() {
        return tenderedAmount;
    }

    public void setTenderedAmount(double tenderedAmount) {
        this.tenderedAmount = tenderedAmount;
    }

    public double getChangeGiven() {
        return changeGiven;
    }

    public void setChangeGiven(double changeGiven) {
        this.changeGiven = changeGiven;
    }
}
