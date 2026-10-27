package com.example.medicalappointment;

import com.example.medicalappointment.exception.ConflictException;
import com.example.medicalappointment.model.*;
import com.example.medicalappointment.repository.*;
import com.example.medicalappointment.service.*;
import com.example.medicalappointment.util.FileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MedicalAppointmentSystemTest {

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private StaffService staffService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private DoctorAvailabilityChecker availabilityChecker;

    @Test
    @DisplayName("Test 1: FileHandler creates files and safely writes/reads pipe-delimited data")
    void testFileHandling() {
        String testFile = "data/test_io.txt";
        File f = FileHandler.ensureFileExists(testFile);
        assertTrue(f.exists(), "File should be created if not exists");

        FileHandler.appendLine(testFile, "TEST001|Sample Data|Unit Test");
        List<String> lines = FileHandler.readAllLines(testFile);
        assertFalse(lines.isEmpty(), "Should read appended lines");
        assertTrue(lines.stream().anyMatch(l -> l.contains("TEST001")), "Line content should match");

        // Clean up test file
        f.delete();
    }

    @Test
    @DisplayName("Test 2: Patient Registration, Encapsulation, and VIP Polymorphism")
    void testPatientCRUDAndPolymorphism() {
        String uniqueUser = "testuser_" + System.currentTimeMillis();
        Patient p = new VIPPatient(null, uniqueUser, "secret123", "Test Patient",
                uniqueUser + "@example.com", "0771122334", "Colombo", "Platinum", 0.20, true);

        // CRUD - Create
        Patient saved = patientService.registerPatient(p);
        assertNotNull(saved.getPatientId());
        assertEquals(uniqueUser, saved.getUsername());

        // Polymorphism check: VIP patient calculates 20% discount
        double fee = 1000.0;
        double discount = saved.calculateDiscount(fee);
        assertEquals(200.0, discount, 0.01, "VIP discount should be 20%");

        // CRUD - Read
        Patient fetched = patientService.getPatientById(saved.getPatientId());
        assertEquals("Test Patient", fetched.getName());

        // CRUD - Update
        fetched.setName("Updated Patient Name");
        Patient updated = patientService.updatePatient(saved.getPatientId(), fetched);
        assertEquals("Updated Patient Name", updated.getName());

        // CRUD - Delete
        boolean deleted = patientService.deletePatient(saved.getPatientId());
        assertTrue(deleted, "Patient should be deleted");
    }

    @Test
    @DisplayName("Test 3: Doctor Subclasses, Inheritance, and Fee Polymorphism")
    void testDoctorSubclassesAndPolymorphism() {
        Doctor genDoc = new GeneralDoctor("DOC-T1", "Dr. General", "General Practice", "0771", "Available", 2000.0, "Room 1");
        Doctor specDoc = new SpecialistDoctor("DOC-T2", "Dr. Spec", "Cardiology", "0772", "Available", 3000.0, "MD");
        Doctor surgDoc = new Surgeon("DOC-T3", "Dr. Surg", "Orthopedics", "0773", "Available", 5000.0, "OT-1");

        // Polymorphism: Consultation fee calculation differs across subclasses
        // Emergency consultation
        assertTrue(genDoc.calculateConsultationFee(true) > 2000.0);
        assertTrue(specDoc.calculateConsultationFee(true) > 3000.0);
        assertTrue(surgDoc.calculateConsultationFee(true) > 5000.0);

        // Verify inheritance from Staff
        assertTrue(genDoc instanceof Staff, "Doctor must inherit from Staff");
        assertNotNull(genDoc.getRoleDescription());
    }

    @Test
    @DisplayName("Test 4: Appointment Booking & Conflict Detection (Abstraction)")
    void testAppointmentConflictDetection() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        assertFalse(doctors.isEmpty(), "Doctors should exist");
        Doctor doctor = doctors.get(0);

        List<Patient> patients = patientService.getAllPatients();
        assertFalse(patients.isEmpty(), "Patients should exist");
        Patient patient = patients.get(0);

        String testDate = "2027-12-01";
        String testTime = "10:30";

        // Book 1st appointment
        Appointment apt1 = new Appointment(null, patient.getPatientId(), doctor.getDoctorId(),
                testDate, testTime, "Scheduled", "General consultation", "Standard");
        Appointment booked1 = appointmentService.bookAppointment(apt1);
        assertNotNull(booked1.getAppointmentId());

        // Attempt booking a 2nd appointment at the exact same slot -> Must throw ConflictException
        Appointment apt2 = new Appointment(null, patient.getPatientId(), doctor.getDoctorId(),
                testDate, testTime, "Scheduled", "Double booking check", "Standard");

        assertThrows(ConflictException.class, () -> {
            appointmentService.bookAppointment(apt2);
        }, "Booking at identical doctor/date/time slot should trigger ConflictException");

        // Cleanup
        appointmentService.deleteAppointment(booked1.getAppointmentId());
    }

    @Test
    @DisplayName("Test 5: Payment Processing & Payment Method Polymorphism")
    void testPaymentPolymorphicProcessing() {
        List<Patient> patients = patientService.getAllPatients();
        List<Appointment> appointments = appointmentService.getAllAppointments();
        assertFalse(patients.isEmpty());
        assertFalse(appointments.isEmpty());

        String patId = patients.get(0).getPatientId();
        String aptId = appointments.get(0).getAppointmentId();

        // Process Card Payment
        Payment cardPay = paymentService.processPayment(aptId, patId, 3000.0, "CARD", "4242");
        assertNotNull(cardPay.getPaymentId());
        assertTrue(cardPay instanceof CardPayment, "Should create CardPayment instance");
        assertTrue(cardPay.calculateProcessingFee() > 0, "Card fee should be non-zero");

        // Process Cash Payment
        Payment cashPay = paymentService.processPayment(aptId, patId, 2000.0, "CASH", "Counter 1");
        assertTrue(cashPay instanceof CashPayment, "Should create CashPayment instance");
        assertEquals(0.0, cashPay.calculateProcessingFee(), "Cash processing fee should be 0");
    }

    @Test
    @DisplayName("Test 6: Staff Hierarchy & Review Badges")
    void testStaffAndReviews() {
        Staff nurse = new Nurse("STF-TEST", "Nurse Joy", "077889900", "Pediatrics", 60000.0, "Ward 2", "Night");
        assertEquals("Nurse", nurse.getPosition());
        assertTrue(nurse.calculateDutyAllowance() > 0, "Duty allowance should be calculated");

        Review vRev = new VerifiedReview("REV-TEST", "PAT-101", "DOC-101", 5, "Great doctor", "2026-10-07", "APT-101", "Surgery");
        assertTrue(vRev.isVerifiedPatient(), "Verified review should flag true for verified patient");
        assertTrue(vRev.getBadgeLabel().contains("Verified"), "Badge should indicate verification");
    }

    @Test
    @DisplayName("Test 7: Appointment Date Validation prevents booking past dates")
    void testPastAppointmentDateValidation() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        List<Patient> patients = patientService.getAllPatients();
        assertFalse(doctors.isEmpty());
        assertFalse(patients.isEmpty());

        // Booking with a past date (e.g. 2020-01-01) must fail with ValidationException
        Appointment pastApt = new Appointment(null, patients.get(0).getPatientId(), doctors.get(0).getDoctorId(),
                "2020-01-01", "10:00", "Scheduled", "Past checkup", "Standard");

        assertThrows(com.example.medicalappointment.exception.ValidationException.class, () -> {
            appointmentService.bookAppointment(pastApt);
        }, "Booking an appointment in the past must throw ValidationException");
    }
}
