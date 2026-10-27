package com.example.medicalappointment.model;

/**
 * FollowUpAppointment class demonstrating:
 * - Inheritance: Subclass of Appointment.
 * - Polymorphism: Overrides priority level and estimated duration.
 */
public class FollowUpAppointment extends Appointment {

    // Encapsulation - Specific field
    private String previousAppointmentId;

    public FollowUpAppointment() {
        super();
        setAppointmentType("FollowUp");
        this.previousAppointmentId = "None";
    }

    public FollowUpAppointment(String appointmentId, String patientId, String doctorId,
                               String appointmentDate, String appointmentTime,
                               String status, String reason, String previousAppointmentId) {
        super(appointmentId, patientId, doctorId, appointmentDate, appointmentTime, status, reason, "FollowUp");
        this.previousAppointmentId = previousAppointmentId;
    }

    // Polymorphism - Priority level 2 for routine follow up
    @Override
    public int getPriorityLevel() {
        return 2;
    }

    // Polymorphism - Shorter duration needed
    @Override
    public int getEstimatedDurationMinutes() {
        return 15;
    }

    @Override
    protected String getExtraData() {
        return "PrevApt:" + previousAppointmentId;
    }

    public String getPreviousAppointmentId() {
        return previousAppointmentId;
    }

    public void setPreviousAppointmentId(String previousAppointmentId) {
        this.previousAppointmentId = previousAppointmentId;
    }
}
