package com.example.medicalappointment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Medical Appointment Scheduling System
 * Main Application Entry Point
 */
@SpringBootApplication
public class MedicalAppointmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicalAppointmentApplication.class, args);
        System.out.println("==================================================================");
        System.out.println(" Medical Appointment Scheduling System is RUNNING!");
        System.out.println(" Access URL: http://localhost:8080");
        System.out.println(" Data Storage: File-based system in 'data/' directory");
        System.out.println("==================================================================");
    }
}
