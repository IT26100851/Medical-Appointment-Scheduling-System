package com.example.medicalappointment.model;

/**
 * EmergencyAppointment class demonstrating:
 * - Inheritance: Subclass of Appointment.
 * - Polymorphism: Overrides priority level and estimated duration.
 */
public class EmergencyAppointment extends Appointment {

    // Encapsulation - Specific field
    private String emergencySeverity; // "High", "Critical"

    public EmergencyAppointment() {
        super();
        setAppointmentType("Emergency");
        this.emergencySeverity = "High";
    }

    public EmergencyAppointment(String appointmentId, String patientId, String doctorId,
                                String appointmentDate, String appointmentTime,
                                String status, String reason, String emergencySeverity) {
        super(appointmentId, patientId, doctorId, appointmentDate, appointmentTime, status, reason, "Emergency");
        this.emergencySeverity = emergencySeverity;
    }

    // Polymorphism - Highest priority level (3)
    @Override
    public int getPriorityLevel() {
        return 3;
    }

    // Polymorphism - Emergency consultations require longer evaluation
    @Override
    public int getEstimatedDurationMinutes() {
        return 45;
    }

    @Override
    protected String getExtraData() {
        return "Severity:" + emergencySeverity;
    }

    public String getEmergencySeverity() {
        return emergencySeverity;
    }

    public void setEmergencySeverity(String emergencySeverity) {
        this.emergencySeverity = emergencySeverity;
    }
}
