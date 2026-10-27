package com.example.medicalappointment.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * DataInitializer Component
 * Automatically creates the 'data/' directory and initializes realistic sample data files
 * on application startup if the files do not exist or are empty.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Override
    public void run(String... args) {
        System.out.println("[DataInitializer] Verifying data storage directory and sample records...");
        initPatients();
        initDoctors();
        initAppointments();
        initPayments();
        initStaff();
        initReviews();
        System.out.println("[DataInitializer] Data storage initialization completed successfully.");
    }

    private void initPatients() {
        String path = "data/patients.txt";
        File file = FileHandler.ensureFileExists(path);
        if (file.length() == 0) {
            List<String> records = Arrays.asList(
                    "# patientId|username|password|name|email|phone|address|patientType|extraData",
                    "PAT-101|john_doe|pass123|John Doe|john.doe@example.com|0771234567|45 Galle Road, Colombo 03|VIP|Level:Gold;Discount:0.15;Priority:true",
                    "PAT-102|sarah_smith|pass123|Sarah Smith|sarah.smith@example.com|0719876543|12 Flower Road, Colombo 07|Regular|Points:150",
                    "PAT-103|michael_clarke|pass123|Michael Clarke|michael.c@example.com|0785551234|88 Kandy Road, Kadawatha|VIP|Level:Platinum;Discount:0.20;Priority:true",
                    "PAT-104|anoma_perera|pass123|Anoma Perera|anoma.p@example.com|0764433221|23 High Level Rd, Nugegoda|Regular|Points:45"
            );
            FileHandler.writeAllLines(path, records);
            System.out.println("[DataInitializer] Initialized sample patients in " + path);
        }
    }

    private void initDoctors() {
        String path = "data/doctors.txt";
        File file = FileHandler.ensureFileExists(path);
        if (file.length() == 0) {
            List<String> records = Arrays.asList(
                    "# doctorId|name|specialization|contactNumber|availabilityStatus|doctorType|consultationFee|extraData",
                    "DOC-101|Dr. Samantha Perera|Cardiology|0712345678|Available|SpecialistDoctor|4000.00|BoardCertification:MD, FACC Consultant Cardiologist",
                    "DOC-102|Dr. David Fernando|General Medicine|0778765432|Available|GeneralDoctor|2000.00|ClinicRoom:Room 102 - OPD Wing",
                    "DOC-103|Dr. Aruni Jayawardena|Orthopedics|0753344556|Available|Surgeon|5500.00|OT:OT-2 Surgical Wing",
                    "DOC-104|Dr. Kusal Mendis|Pediatrics|0721122334|Busy|SpecialistDoctor|3000.00|BoardCertification:MBBS, DCH Consultant Pediatrician",
                    "DOC-105|Dr. Niluka Silva|Dermatology|0709988776|Available|GeneralDoctor|2500.00|ClinicRoom:Room 105 - Dermatology Clinic"
            );
            FileHandler.writeAllLines(path, records);
            System.out.println("[DataInitializer] Initialized sample doctors in " + path);
        }
    }

    private void initAppointments() {
        String path = "data/appointments.txt";
        File file = FileHandler.ensureFileExists(path);
        if (file.length() == 0) {
            List<String> records = Arrays.asList(
                    "# appointmentId|patientId|doctorId|appointmentDate|appointmentTime|status|reason|appointmentType|extraData",
                    "APT-101|PAT-101|DOC-101|2026-10-15|09:30|Scheduled|Routine cardiac checkup and ECG review|Standard|None",
                    "APT-102|PAT-102|DOC-102|2026-10-16|10:00|Scheduled|Seasonal flu and persistent fever consultation|Standard|None",
                    "APT-103|PAT-103|DOC-103|2026-10-14|14:00|Completed|Knee joint arthroscopy follow-up consultation|Emergency|Severity:High Priority",
                    "APT-104|PAT-104|DOC-105|2026-10-18|11:15|Scheduled|Skin rash and allergic reaction follow-up|FollowUp|PrevApt:APT-090",
                    "APT-105|PAT-101|DOC-102|2026-10-10|15:30|Cancelled|Cancelled due to patient personal travel|Standard|None"
            );
            FileHandler.writeAllLines(path, records);
            System.out.println("[DataInitializer] Initialized sample appointments in " + path);
        }
    }

    private void initPayments() {
        String path = "data/payments.txt";
        File file = FileHandler.ensureFileExists(path);
        if (file.length() == 0) {
            List<String> records = Arrays.asList(
                    "# paymentId|appointmentId|patientId|amount|paymentDate|paymentStatus|paymentMethod|transactionReference|extraData",
                    "PAY-101|APT-103|PAT-103|4400.00|2026-10-14|PAID|CARD|TXN-991201|CardType:Visa;Last4:4242;Auth:AUTH-8821",
                    "PAY-102|APT-102|PAT-102|1995.00|2026-10-16|PAID|CASH|TXN-991202|Cashier:Front Desk Cashier;Tendered:2000.00;Change:5.00",
                    "PAY-103|APT-101|PAT-101|3400.00|2026-10-15|PAID|ONLINE|TXN-991203|Gateway:SecurePay Online;GatewayId:GW-883719"
            );
            FileHandler.writeAllLines(path, records);
            System.out.println("[DataInitializer] Initialized sample payments in " + path);
        }
    }

    private void initStaff() {
        String path = "data/staff.txt";
        File file = FileHandler.ensureFileExists(path);
        if (file.length() == 0) {
            List<String> records = Arrays.asList(
                    "# staffId|name|contactNumber|position|department|salary|additionalInfo",
                    "STF-101|Nurse Mary Watson|0774433221|Nurse|Emergency Care|65000.00|Ward:Emergency ICU;Shift:Night",
                    "STF-102|Sunil Wickramasinghe|0715566778|Receptionist|Patient Relations|45000.00|Desk:Desk 01;Languages:English, Sinhala, Tamil",
                    "STF-103|Nurse Ruwani Jayasinghe|0782233445|Nurse|Pediatrics Ward|60000.00|Ward:Pediatrics Ward 3;Shift:Day",
                    "STF-104|Dr. Samantha Perera|0712345678|Doctor|Cardiology|180000.00|Status:Available",
                    "STF-105|Dr. David Fernando|0778765432|Doctor|General Medicine|150000.00|Status:Available"
            );
            FileHandler.writeAllLines(path, records);
            System.out.println("[DataInitializer] Initialized sample staff in " + path);
        }
    }

    private void initReviews() {
        String path = "data/reviews.txt";
        File file = FileHandler.ensureFileExists(path);
        if (file.length() == 0) {
            List<String> records = Arrays.asList(
                    "# reviewId|patientId|doctorId|rating|comment|date|reviewType|extraData",
                    "REV-101|PAT-101|DOC-101|5|Dr. Samantha was extremely thorough and kind. She answered all my questions regarding heart health.|2026-10-01|VerifiedReview|Apt:APT-101;Treatment:Cardiac Consultation",
                    "REV-102|PAT-102|DOC-102|4|Prompt diagnosis and very friendly bedside manner. Waiting time was minimal.|2026-10-02|PatientReview|Regular",
                    "REV-103|PAT-103|DOC-103|5|Superb orthopedic surgeon. Knee mobility recovered much faster than anticipated.|2026-10-03|VerifiedReview|Apt:APT-103;Treatment:Joint Surgery",
                    "REV-104|PAT-104|DOC-105|5|Dr. Niluka provided clear dietary advice and an effective skin ointment. Very pleased!|2026-10-04|PatientReview|Regular"
            );
            FileHandler.writeAllLines(path, records);
            System.out.println("[DataInitializer] Initialized sample reviews in " + path);
        }
    }
}
