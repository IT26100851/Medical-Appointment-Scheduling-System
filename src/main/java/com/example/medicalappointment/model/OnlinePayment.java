package com.example.medicalappointment.model;

/**
 * OnlinePayment class demonstrating:
 * - Inheritance: Subclass of Payment.
 * - Polymorphism: Implementation of online gateway fee calculation (2.0%)
 *   and online transaction receipt formatting.
 */
public class OnlinePayment extends Payment {

    // Encapsulation - Specific online gateway fields
    private String paymentGateway; // "PayPal", "PayHere", "Stripe Mock"
    private String gatewayTransactionId;

    public OnlinePayment() {
        super();
        setPaymentMethod("ONLINE");
        this.paymentGateway = "Online Portal";
        this.gatewayTransactionId = "GTW-MOCK-001";
    }

    public OnlinePayment(String paymentId, String appointmentId, String patientId, double amount,
                         String paymentDate, String paymentStatus, String transactionReference,
                         String paymentGateway, String gatewayTransactionId) {
        super(paymentId, appointmentId, patientId, amount, paymentDate, paymentStatus, "ONLINE", transactionReference);
        this.paymentGateway = paymentGateway;
        this.gatewayTransactionId = gatewayTransactionId;
    }

    // Polymorphism - 2% online gateway surcharge
    @Override
    public double calculateProcessingFee() {
        return getAmount() * 0.020;
    }

    @Override
    public String getPaymentReceiptDetails() {
        return "Online Payment via " + paymentGateway + " [Gateway Ref: " + gatewayTransactionId + "]";
    }

    @Override
    protected String getPaymentSpecificData() {
        return "Gateway:" + paymentGateway + ";GatewayId:" + gatewayTransactionId;
    }

    public String getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(String paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public String getGatewayTransactionId() {
        return gatewayTransactionId;
    }

    public void setGatewayTransactionId(String gatewayTransactionId) {
        this.gatewayTransactionId = gatewayTransactionId;
    }
}
