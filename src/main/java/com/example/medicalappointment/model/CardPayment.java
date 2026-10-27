package com.example.medicalappointment.model;

/**
 * CardPayment class demonstrating:
 * - Inheritance: Subclass of Payment.
 * - Polymorphism: Implementation of fee calculation (1.5% merchant surcharge)
 *   and secure card receipt formatting.
 */
public class CardPayment extends Payment {

    // Encapsulation - Specific card payment fields
    private String cardType; // "Visa", "MasterCard", "Amex"
    private String cardLast4;
    private String authorizationCode;

    public CardPayment() {
        super();
        setPaymentMethod("CARD");
        this.cardType = "Visa";
        this.cardLast4 = "4242";
        this.authorizationCode = "AUTH-OK";
    }

    public CardPayment(String paymentId, String appointmentId, String patientId, double amount,
                       String paymentDate, String paymentStatus, String transactionReference,
                       String cardType, String cardLast4, String authorizationCode) {
        super(paymentId, appointmentId, patientId, amount, paymentDate, paymentStatus, "CARD", transactionReference);
        this.cardType = cardType;
        this.cardLast4 = cardLast4;
        this.authorizationCode = authorizationCode;
    }

    // Polymorphism - Card processing adds a standard 1.5% processing fee
    @Override
    public double calculateProcessingFee() {
        return getAmount() * 0.015;
    }

    @Override
    public String getPaymentReceiptDetails() {
        return "Card Payment (" + cardType + " ****" + cardLast4 + ") [Auth Code: " + authorizationCode + "]";
    }

    @Override
    protected String getPaymentSpecificData() {
        return "CardType:" + cardType + ";Last4:" + cardLast4 + ";Auth:" + authorizationCode;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardLast4() {
        return cardLast4;
    }

    public void setCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }
}
