package com.example.medicalappointment.model;

/**
 * Nurse class demonstrating:
 * - Inheritance: Extends abstract Staff class.
 * - Polymorphism: Implements abstract methods getRoleDescription and calculateDutyAllowance.
 */
public class Nurse extends Staff {

    // Encapsulation - Specific nurse attributes
    private String assignedWard;
    private String shiftTiming; // "Day", "Night", "Rotating"

    public Nurse() {
        super();
        setPosition("Nurse");
    }

    public Nurse(String staffId, String name, String contactNumber, String department,
                 double salary, String assignedWard, String shiftTiming) {
        super(staffId, name, contactNumber, "Nurse", department, salary);
        this.assignedWard = assignedWard;
        this.shiftTiming = shiftTiming;
    }

    // Polymorphism - Implementation of abstract method
    @Override
    public String getRoleDescription() {
        return "Registered Healthcare Nurse - Dedicated to " + assignedWard + " (" + shiftTiming + " Shift)";
    }

    // Polymorphism - Night shift nurses receive higher shift allowance
    @Override
    public double calculateDutyAllowance() {
        if ("Night".equalsIgnoreCase(shiftTiming)) {
            return getSalary() * 0.15; // 15% night differential
        }
        return getSalary() * 0.05; // 5% day allowance
    }

    @Override
    protected String getStaffExtraDetails() {
        return "Ward:" + assignedWard + ";Shift:" + shiftTiming;
    }

    public String getAssignedWard() {
        return assignedWard;
    }

    public void setAssignedWard(String assignedWard) {
        this.assignedWard = assignedWard;
    }

    public String getShiftTiming() {
        return shiftTiming;
    }

    public void setShiftTiming(String shiftTiming) {
        this.shiftTiming = shiftTiming;
    }
}
