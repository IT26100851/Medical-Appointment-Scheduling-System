package com.example.medicalappointment.model;

/**
 * Appointment class demonstrating:
 * - Encapsulation: Private fields with accessor/mutator methods.
 * - Inheritance: Subclassed by EmergencyAppointment and FollowUpAppointment.
 * - Polymorphism: Methods for duration estimation, priority calculation, and cancellation rules.
 */
public class Appointment {

    // Encapsulation - Private fields
    private String appointmentId;
    private String patientId;
    private String doctorId;
    private String appointmentDate; // YYYY-MM-DD
    private String appointmentTime; // HH:mm (e.g. "09:30")
    private String status;          // "Scheduled", "Completed", "Cancelled", "In-Progress"
    private String reason;
    private String appointmentType; // "Standard", "Emergency", "FollowUp"

    public Appointment() {
        this.status = "Scheduled";
        this.appointmentType = "Standard";
    }

    public Appointment(String appointmentId, String patientId, String doctorId,
                       String appointmentDate, String appointmentTime,
                       String status, String reason, String appointmentType) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = (status == null || status.isBlank()) ? "Scheduled" : status;
        this.reason = reason;
        this.appointmentType = (appointmentType == null || appointmentType.isBlank()) ? "Standard" : appointmentType;
    }

    // Polymorphism - Priority level (1: normal, 2: follow-up, 3: urgent)
    public int getPriorityLevel() {
        return 1;
    }

    // Polymorphism - Consultation duration in minutes
    public int getEstimatedDurationMinutes() {
        return 20; // 20 minutes standard
    }

    // Polymorphism - Cancellation check: can this appointment be cancelled?
    public boolean canBeCancelled() {
        return !"Cancelled".equalsIgnoreCase(status) && !"Completed".equalsIgnoreCase(status);
    }

    // File Handling serialization for appointments.txt
    public String toFileString() {
        return String.join("|",
                safe(appointmentId),
                safe(patientId),
                safe(doctorId),
                safe(appointmentDate),
                safe(appointmentTime),
                safe(status),
                safe(reason),
                safe(appointmentType),
                safe(getExtraData())
        );
    }

    protected String getExtraData() {
        return "None";
    }

    private String safe(String val) {
        return (val == null) ? "" : val.replace("|", " ").trim();
    }

    // Getters and Setters
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

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }
}
