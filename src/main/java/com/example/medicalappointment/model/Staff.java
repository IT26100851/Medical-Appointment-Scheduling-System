package com.example.medicalappointment.model;

/**
 * Abstract Staff base class demonstrating:
 * - Abstraction: Cannot be directly instantiated; defines abstract methods
 *   that subclasses must implement.
 * - Encapsulation: Private member variables with getters and setters.
 * - Inheritance: Extended by Doctor, Nurse, and Receptionist.
 */
public abstract class Staff {

    // Encapsulation - Private fields
    private String staffId;
    private String name;
    private String contactNumber;
    private String position;
    private String department;
    private double salary;

    public Staff() {
    }

    public Staff(String staffId, String name, String contactNumber, String position, String department, double salary) {
        this.staffId = staffId;
        this.name = name;
        this.contactNumber = contactNumber;
        this.position = position;
        this.department = department;
        this.salary = salary;
    }

    // Abstraction & Polymorphism - Abstract method defining role description
    public abstract String getRoleDescription();

    // Abstraction & Polymorphism - Abstract method defining allowance calculation
    public abstract double calculateDutyAllowance();

    // Reusable file serialization string
    public String toStaffFileString() {
        return String.join("|",
                safe(staffId),
                safe(name),
                safe(contactNumber),
                safe(position),
                safe(department),
                String.format("%.2f", salary),
                safe(getStaffExtraDetails())
        );
    }

    protected abstract String getStaffExtraDetails();

    protected String safe(String val) {
        return (val == null) ? "" : val.replace("|", " ").trim();
    }

    // Getters and Setters
    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }
}
