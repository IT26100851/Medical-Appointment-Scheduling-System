# CareSync - Medical Appointment Scheduling System

**SE1020 Object Oriented Programming - Semester Project**

A full-stack, web-based Medical Appointment Scheduling System built with **Java**, **Spring Boot**, **Bootstrap 5**, and native **Java File I/O Persistence** (`.txt` files).

---

## 🌟 Key Features

1. **Patient Management:**
   - Registration with input validations (email regex, phone number, duplicate username protection).
   - Authentication and session tracking.
   - Profile view and real-time updates.
   - Regular and VIP patient membership tiers with dynamic consultation discounts.

2. **Doctor Management:**
   - Clinical doctor directory with specialization filters (Cardiology, OPD, Orthopedics, Pediatrics, Dermatology).
   - Real-time search by doctor name, specialization, and availability status.
   - Multi-level doctor hierarchy (`GeneralDoctor`, `SpecialistDoctor`, `Surgeon`).

3. **Appointment Scheduling:**
   - Booking interface with live slot conflict and double-booking detection.
   - Rescheduling slots and cancellation management.
   - Polymorphic appointment types (`Standard`, `Emergency`, `FollowUp`).

4. **Medical Billing & Simulated Payments:**
   - Automatic bill calculation with VIP patient discounts applied.
   - Simulated checkout supporting `CashPayment`, `CardPayment`, and `OnlinePayment`.
   - Printable transaction receipts and payment history logs.

5. **Hospital Staff Management:**
   - Complete workforce directory covering `Doctor`, `Nurse`, and `Receptionist`.
   - Polymorphic allowance and shift calculation.

6. **Feedback & Reviews:**
   - Star ratings (1 to 5) and feedback comments.
   - Verified patient badges linked to completed appointments.

7. **Admin Dashboard:**
   - Real-time analytics (Total Patients, Doctors, Staff, Appointments, Revenue).
   - Complete CRUD management tables for all six entities.

---

## 🛠️ Technologies Used

- **Backend:** Java 17+ / Spring Boot 3.3.4 (Spring Web, Spring Validation)
- **Frontend:** HTML5, CSS3, JavaScript (ES6+ Fetch API), Bootstrap 5.3, Bootstrap Icons
- **Storage:** Java File I/O (`BufferedReader`, `BufferedWriter`, `FileReader`, `FileWriter`, `Files`, `Path`)
- **Build Tool:** Apache Maven
- **IDE:** IntelliJ IDEA

---

## 📐 Object Oriented Programming (OOP) Implementation

| OOP Concept | Implementation in Codebase |
| :--- | :--- |
| **Encapsulation** | All entity fields are declared `private` with controlled public getters and setters with validation rules. |
| **Inheritance** | • `Patient` &rarr; `RegularPatient`, `VIPPatient`<br>• `Staff` &rarr; `Doctor`, `Nurse`, `Receptionist`<br>• `Doctor` &rarr; `GeneralDoctor`, `SpecialistDoctor`, `Surgeon`<br>• `Payment` &rarr; `CashPayment`, `CardPayment`, `OnlinePayment`<br>• `Review` &rarr; `PatientReview`, `VerifiedReview` |
| **Polymorphism** | Dynamic method dispatch in `calculateDiscount()`, `calculateConsultationFee()`, `calculateProcessingFee()`, `getRoleDescription()`, and `getBadgeLabel()`. |
| **Abstraction** | Abstract classes (`Staff`, `Payment`) defining abstract methods, and interfaces (`DoctorAvailabilityChecker`) decoupling scheduling logic from doctor availability checks. |
| **Information Hiding** | Synchronization locks, private file serialization helpers, and protected member details prevent unintended external modification. |

---

## 💾 File Handling & Storage Architecture

All data is stored in plain text files inside the `data/` directory using a pipe delimiter (`|`):

| File Path | Entity Description | Record Format Sample |
| :--- | :--- | :--- |
| `data/patients.txt` | Patient accounts | `PAT-101\|john_doe\|pass123\|John Doe\|john@example.com\|0771234567\|Colombo\|VIP\|Level:Gold...` |
| `data/doctors.txt` | Doctor directory | `DOC-101\|Dr. Samantha Perera\|Cardiology\|0712345678\|Available\|SpecialistDoctor\|4000.00...` |
| `data/appointments.txt` | Scheduled appointments | `APT-101\|PAT-101\|DOC-101\|2026-10-15\|09:30\|Scheduled\|Cardiac checkup\|Standard\|None` |
| `data/payments.txt` | Billing transactions | `PAY-101\|APT-103\|PAT-103\|4400.00\|2026-10-14\|PAID\|CARD\|TXN-991201\|CardType:Visa...` |
| `data/staff.txt` | Hospital workforce | `STF-101\|Nurse Mary Watson\|0774433221\|Nurse\|Emergency Care\|65000.00\|Ward:Emergency ICU...` |
| `data/reviews.txt` | Patient feedback | `REV-101\|PAT-101\|DOC-101\|5\|Extremely thorough and kind.\|2026-10-01\|VerifiedReview...` |

### Resilient File Operations:
- **Auto-Initialization:** `DataInitializer` automatically creates the `data/` directory and pre-populates realistic sample records if files are missing or empty.
- **Atomic Operations:** Uses `BufferedWriter` for writes and `BufferedReader` for fast line reads.
- **Thread Safety:** `synchronized (FILE_LOCK)` blocks guarantee safe concurrent access.

---

## 📂 Project Directory Structure

```
D:/1Y2Sem/OOP/
├── pom.xml                                  # Maven project configuration
├── mvnw.cmd / mvnw                          # Maven wrapper executables
├── .gitignore                               # Git ignore configuration
├── README.md                                # Project documentation
├── VIVA_NOTES.md                            # Complete viva preparation & oral exam Q&A
├── data/                                    # Persistent text storage
│   ├── patients.txt
│   ├── doctors.txt
│   ├── appointments.txt
│   ├── payments.txt
│   ├── staff.txt
│   └── reviews.txt
└── src/
    ├── main/
    │   ├── java/com/example/medicalappointment/
    │   │   ├── MedicalAppointmentApplication.java   # Spring Boot Application Entry Point
    │   │   ├── model/                               # Domain models & OOP class hierarchies
    │   │   │   ├── Patient.java
    │   │   │   ├── RegularPatient.java
    │   │   │   ├── VIPPatient.java
    │   │   │   ├── Staff.java (abstract)
    │   │   │   ├── Doctor.java
    │   │   │   ├── GeneralDoctor.java
    │   │   │   ├── SpecialistDoctor.java
    │   │   │   ├── Surgeon.java
    │   │   │   ├── Nurse.java
    │   │   │   ├── Receptionist.java
    │   │   │   ├── Appointment.java
    │   │   │   ├── EmergencyAppointment.java
    │   │   │   ├── FollowUpAppointment.java
    │   │   │   ├── Payment.java (abstract)
    │   │   │   ├── CashPayment.java
    │   │   │   ├── CardPayment.java
    │   │   │   ├── OnlinePayment.java
    │   │   │   ├── Review.java
    │   │   │   ├── PatientReview.java
    │   │   │   ├── VerifiedReview.java
    │   │   │   └── ... (DTOs)
    │   │   ├── repository/                          # File CRUD operations
    │   │   │   ├── PatientRepository.java
    │   │   │   ├── DoctorRepository.java
    │   │   │   ├── AppointmentRepository.java
    │   │   │   ├── PaymentRepository.java
    │   │   │   ├── StaffRepository.java
    │   │   │   └── ReviewRepository.java
    │   │   ├── service/                             # Business logic & abstraction
    │   │   │   ├── DoctorAvailabilityChecker.java (Interface)
    │   │   │   ├── DefaultDoctorAvailabilityChecker.java
    │   │   │   ├── PatientService.java
    │   │   │   ├── DoctorService.java
    │   │   │   ├── AppointmentService.java
    │   │   │   ├── PaymentService.java
    │   │   │   ├── StaffService.java
    │   │   │   ├── ReviewService.java
    │   │   │   └── AuthService.java
    │   │   ├── controller/                          # REST API controllers
    │   │   │   ├── PatientController.java
    │   │   │   ├── DoctorController.java
    │   │   │   ├── AppointmentController.java
    │   │   │   ├── PaymentController.java
    │   │   │   ├── StaffController.java
    │   │   │   ├── ReviewController.java
    │   │   │   ├── AuthController.java
    │   │   │   └── DashboardController.java
    │   │   ├── util/                                # Java I/O FileHandler & Seeder
    │   │   │   ├── FileHandler.java
    │   │   │   └── DataInitializer.java
    │   │   └── exception/                           # Error handling & ControllerAdvice
    │   └── resources/
    │       ├── application.properties
    │       └── static/                              # Frontend web resources
    │           ├── index.html                       # Lean root shell loading modular parts
    │           ├── parts/                           # Modular HTML components & view parts
    │           │   ├── navbar.html                  # Role navigation & demo switcher
    │           │   ├── footer.html                  # Footer component
    │           │   ├── modals.html                  # Reschedule & admin modals
    │           │   ├── staff-dashboard.html         # Dedicated medical staff operational console
    │           │   ├── admin-dashboard.html         # Hospital administrative metrics & revenue
    │           │   ├── appointment-booking.html     # Booking form with future datepicker constraint
    │           │   └── ... (23 modular HTML files)
    │           ├── css/style.css                    # Medical theme stylesheet
    │           └── js/app.js                        # Client modular loader, role guards & API integration
    └── test/java/com/example/medicalappointment/
        └── MedicalAppointmentSystemTest.java       # Comprehensive JUnit 5 test suite (7 tests)
```

---

## 🚀 How to Run the Project in IntelliJ IDEA

### Method 1: Using IntelliJ IDEA GUI (Recommended)
1. Launch **IntelliJ IDEA**.
2. Select **File &rarr; Open...** and navigate to `D:\1Y2Sem\OOP`.
3. IntelliJ will automatically detect the `pom.xml` file and configure it as a **Maven Project**.
4. Allow IntelliJ a few moments to index dependencies.
5. In the Project tool window, navigate to:  
   `src/main/java/com/example/medicalappointment/MedicalAppointmentApplication.java`
6. Right-click on `MedicalAppointmentApplication.java` and click **Run 'MedicalAppointmentApplication'** (or click the green Play icon).
7. Open your browser and navigate to:  
   👉 **http://localhost:8080**

### Method 2: Using the Command Line / Terminal
Open PowerShell or Terminal in `D:\1Y2Sem\OOP` and execute:
```powershell
.\mvnw spring-boot:run
```
Or with existing Maven:
```powershell
mvn spring-boot:run
```

---

## 🔑 Demo Login Credentials

The application includes an instant **Role Switcher** at the very top of the webpage for fast demonstration during the viva. Alternatively, you can log in through the Login form:

| Role | Username | Password | Notes |
| :--- | :--- | :--- | :--- |
| **Patient (VIP Gold)** | `john_doe` | `pass123` | Receives 15% VIP discount on all consultations |
| **Patient (Regular)** | `sarah_smith` | `pass123` | Standard patient membership |
| **Admin** | `admin` | `admin123` | Full access to Management CRUD panels & stats |
| **Staff** | `staff` | `staff123` | Access to clinic staff dashboard & duty logs |

---

## 🧪 Testing the Application

Automated unit and integration tests are located in `src/test/java/.../MedicalAppointmentSystemTest.java`.  
To run the automated test suite:
```powershell
.\mvnw test
```
The test suite verifies:
1. `FileHandler` safely creating files and reading/writing pipe-delimited data.
2. Patient registration, encapsulation, and VIP polymorphism.
3. Doctor subclasses (`GeneralDoctor`, `SpecialistDoctor`, `Surgeon`) and polymorphic fees.
4. Appointment booking conflict prevention (`DoctorAvailabilityChecker` abstraction).
5. Payment polymorphic calculations (`Cash`, `Card`, `Online`).
6. Staff duty allowance and verified review badges.

---

## 🐙 Git & GitHub Guidelines

To upload this project to GitHub:
```bash
git init
git add .
git commit -m "Initial commit: Medical Appointment Scheduling System"
git branch -M main
git remote add origin <your-github-repo-url>
git push -u origin main
```

Suggested meaningful commit structure for teamwork reports:
1. `feat: Initial Spring Boot project setup and Maven configuration`
2. `feat: Implement Patient hierarchy (RegularPatient, VIPPatient) and file repository`
3. `feat: Implement Doctor hierarchy (GeneralDoctor, SpecialistDoctor, Surgeon) and search`
4. `feat: Implement Appointment management and DoctorAvailabilityChecker abstraction`
5. `feat: Implement Payment processing and polymorphism (Cash, Card, Online)`
6. `feat: Implement Staff and Review management modules`
7. `feat: Implement reusable FileHandler and automatic DataInitializer`
8. `feat: Build responsive Bootstrap 5 frontend with 21 views and demo role switcher`
9. `test: Add JUnit 5 test suite for CRUD, OOP concepts, and conflict prevention`
10. `docs: Add comprehensive README and VIVA_NOTES documentation`
