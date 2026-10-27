# SE1020 Object Oriented Programming - Viva & Oral Examination Notes

**Project Title:** CareSync - Medical Appointment Scheduling System  
**Module:** SE1020 - Object Oriented Programming  
**Technology Stack:** Java 17+, Spring Boot 3.3.4, HTML5, CSS3, JavaScript, Bootstrap 5, File I/O Storage  

---

## 1. Project Overview & Architecture

### What is the purpose of this project?
The **CareSync Medical Appointment Scheduling System** is a complete web-based healthcare platform designed to streamline doctor appointments, patient management, medical staff tracking, simulated billing/payments, and patient feedback.

Unlike conventional database-driven systems that rely on external database engines like MySQL or PostgreSQL, this system strictly implements **File I/O operations** (`BufferedReader`, `BufferedWriter`, `FileReader`, `FileWriter`, `Files`, `Path`) storing records in pipe-delimited text files (`data/*.txt`). This satisfies the core SE1020 academic requirement of understanding lower-level input/output operations, streams, and file persistence while upholding clean **Object Oriented Design principles**.

### High-Level Architecture
The project follows a 4-Tier Architecture:
1. **Frontend Presentation Tier:** Responsive UI built with HTML5, CSS3, JavaScript (Fetch API), and Bootstrap 5. Communicates with backend endpoints via asynchronous JSON REST requests.
2. **Controller Tier (Web/API):** Exposes clean REST endpoints (`@RestController`) mapping HTTP methods (`GET`, `POST`, `PUT`, `DELETE`) to service operations.
3. **Service Business Logic Tier:** Handles core domain validation, appointment conflict detection, dynamic billing calculation, and authentication.
4. **Repository & File Handling Tier:** Manages reading, writing, updating, and deleting records from `data/*.txt` with synchronization and parsing.

```
[Browser / Frontend (HTML5 + Bootstrap 5 + JS)]
                     |
                REST / JSON
                     v
             [Controllers]
    (Patient, Doctor, Appointment, Payment, Staff, Review)
                     |
                     v
              [Services]
    (Business Rules, Availability Check, Billing Calculation)
                     |
                     v
             [Repositories]
   (CRUD operations, File Parsing, Polymorphic Instantiation)
                     |
                     v
            [FileHandler (Java I/O)]
      (BufferedReader / BufferedWriter / FileReader / FileWriter)
                     |
                     v
    [data/*.txt (Pipe-Delimited File Storage)]
```

---

## 2. Explanation of Each Class in the Codebase

### A. Model Classes (`com.example.medicalappointment.model`)

| Class Name | Type | Purpose & Description |
| :--- | :--- | :--- |
| `Patient` | Base Class | Encapsulates patient details (`patientId`, `username`, `password`, `name`, `email`, `phone`, `address`, `patientType`). Defines polymorphic methods `calculateDiscount()` and `getPatientTier()`. |
| `RegularPatient` | Subclass | Extends `Patient`. Represents standard registered patients. Implements flat loyalty points discount if points exceed threshold. |
| `VIPPatient` | Subclass | Extends `Patient`. Represents premium members (Gold/Platinum). Polymorphically overrides `calculateDiscount()` providing a 15-20% percentage discount on consultations. |
| `Staff` | Abstract Base Class | Abstract model for hospital employees (`staffId`, `name`, `contactNumber`, `position`, `department`, `salary`). Defines abstract methods `getRoleDescription()` and `calculateDutyAllowance()`. |
| `Doctor` | Concrete/Subclass | Extends `Staff`. Represents clinical practitioners (`doctorId`, `specialization`, `availabilityStatus`, `consultationFee`, `doctorType`). Multi-level inheritance parent for specialized doctors. |
| `GeneralDoctor` | Subclass | Extends `Doctor`. Represents OPD and general practitioners with clinic room allocation. Overrides consultation fee with lower standard surcharge. |
| `SpecialistDoctor` | Subclass | Extends `Doctor`. Represents board-certified specialists. Overrides consultation fee with specialist equipment surcharge. |
| `Surgeon` | Subclass | Extends `Doctor`. Represents lead surgeons with Operating Theater assignment. Overrides consultation fee with surgical triage surcharge. |
| `Nurse` | Subclass | Extends `Staff`. Represents nursing staff. Overrides `calculateDutyAllowance()` with night-shift differential bonuses. |
| `Receptionist` | Subclass | Extends `Staff`. Represents front desk staff. Overrides `calculateDutyAllowance()` and role description with counter allocation. |
| `Appointment` | Base Class | Encapsulates appointment booking (`appointmentId`, `patientId`, `doctorId`, `appointmentDate`, `appointmentTime`, `status`, `reason`, `appointmentType`). |
| `EmergencyAppointment` | Subclass | Extends `Appointment`. Polymorphically returns high priority (`3`) and longer estimated duration (45 mins). |
| `FollowUpAppointment` | Subclass | Extends `Appointment`. References prior consultation ID. Overrides priority (`2`) and shorter duration (15 mins). |
| `Payment` | Abstract Base Class | Encapsulates billing transactions (`paymentId`, `appointmentId`, `patientId`, `amount`, `paymentDate`, `paymentStatus`, `paymentMethod`). Declares abstract `calculateProcessingFee()` and `getPaymentReceiptDetails()`. |
| `CashPayment` | Subclass | Extends `Payment`. Represents cash settled at hospital counter. Overrides processing fee (`0.0`). |
| `CardPayment` | Subclass | Extends `Payment`. Represents Visa/Mastercard payments. Overrides processing fee (`1.5%` merchant surcharge) and masks card numbers. |
| `OnlinePayment` | Subclass | Extends `Payment`. Represents gateway checkout. Overrides processing fee (`2.0%` gateway charge) and stores gateway transaction reference. |
| `Review` | Base Class | Encapsulates patient feedback (`reviewId`, `patientId`, `doctorId`, `rating`, `comment`, `date`, `reviewType`). |
| `PatientReview` | Subclass | Extends `Review`. Standard public feedback badge. |
| `VerifiedReview` | Subclass | Extends `Review`. Verified consultation badge with associated `appointmentId` and treatment tag. |

### B. Utility and File Handling Layer (`com.example.medicalappointment.util`)

| Class | Purpose |
| :--- | :--- |
| `FileHandler` | Reusable Java I/O helper. Uses `BufferedReader`, `BufferedWriter`, `FileReader`, `FileWriter`, `Files`, and `Path` to safely perform file existence checks, line reads, line appends, and line overwrites with synchronization locks. |
| `DataInitializer` | Implements Spring Boot `CommandLineRunner`. Runs upon server startup, verifies presence of `data/*.txt` files, and seeds realistic demo data if files are missing or empty. |

### C. Repository Layer (`com.example.medicalappointment.repository`)

| Class | Stored File | Functions |
| :--- | :--- | :--- |
| `PatientRepository` | `data/patients.txt` | Complete CRUD for patients, polymorphic instantiation of `RegularPatient` vs `VIPPatient`. |
| `DoctorRepository` | `data/doctors.txt` | Complete CRUD for doctors, specialization and availability filter queries, polymorphic instantiation of `GeneralDoctor`, `SpecialistDoctor`, `Surgeon`. |
| `AppointmentRepository` | `data/appointments.txt` | Complete CRUD for appointments, lookup by patient, doctor, and date/time conflict queries. |
| `PaymentRepository` | `data/payments.txt` | Complete CRUD for payments, patient payment history, polymorphic instantiation of `CashPayment`, `CardPayment`, `OnlinePayment`. |
| `StaffRepository` | `data/staff.txt` | Complete CRUD for staff, polymorphic instantiation of `Nurse`, `Receptionist`, `Doctor`. |
| `ReviewRepository` | `data/reviews.txt` | Complete CRUD for reviews, lookups by doctor and patient, verified review checks. |

### D. Service Layer (`com.example.medicalappointment.service`)

| Class / Interface | Purpose |
| :--- | :--- |
| `DoctorAvailabilityChecker` | **Interface (Abstraction)** defining availability contract `isDoctorAvailable(doctorId, date, time)` and `validateDoctorAvailability(...)`. |
| `DefaultDoctorAvailabilityChecker` | Concrete service implementing `DoctorAvailabilityChecker`. Verifies doctor active status and checks `AppointmentRepository` for date/time booking clashes. |
| `PatientService` | Validates input (email regex, phone, duplicate usernames), generates unique IDs, executes patient CRUD. |
| `DoctorService` | Manages doctor registry, fee validations, and search filters. |
| `AppointmentService` | Validates booking rules using abstraction, coordinates rescheduling and cancellations. |
| `PaymentService` | Calculates discounts for VIP patients, computes processing fees for Card/Online, updates appointment completion status. |
| `StaffService` | Manages hospital workforce registry and duty allowance calculations. |
| `ReviewService` | Validates rating limits (1-5), links reviews to doctor and patient records. |
| `AuthService` | Handles credential verification for `PATIENT`, `ADMIN`, and `STAFF` roles. |

---

## 3. CRUD Explanation

CRUD stands for **Create, Read, Update, Delete**. Every main entity in the system implements all four operations against text files:

```
+------------+------------------------------------+---------------------------------------+
| Operation  | File Handling Technique            | Example in PatientRepository          |
+------------+------------------------------------+---------------------------------------+
| CREATE     | Append mode via BufferedWriter     | FileHandler.appendLine(FILE_PATH, ...) |
| READ       | Line reading via BufferedReader    | FileHandler.readAllLines(FILE_PATH)   |
| UPDATE     | Read all, replace line, overwrite  | FileHandler.writeAllLines(FILE_PATH,.)|
| DELETE     | Read all, filter out, overwrite    | FileHandler.writeAllLines(FILE_PATH,.)|
+------------+------------------------------------+---------------------------------------+
```

### Step-by-Step CRUD Walkthrough:
1. **Create (Save):**
   - New object is converted to a pipe-delimited string via `toFileString()`.
   - `FileHandler.appendLine()` uses `FileWriter(file, true)` to append the line at the end without rewriting the entire file.
2. **Read (Find All / Find By ID):**
   - `FileHandler.readAllLines()` opens a `BufferedReader(new FileReader(file))`.
   - Loops using `while ((line = reader.readLine()) != null)`.
   - Splits tokens using `line.split("\\|", -1)`.
   - Reconstructs Java domain objects polymorphically.
3. **Update (Modify):**
   - Reads all existing records into memory.
   - Matches the target record by unique ID (e.g., `patientId`).
   - Replaces the old serialized string with the new serialized string.
   - Atomically overwrites the file with the updated list using `FileWriter(file, false)`.
4. **Delete (Remove):**
   - Reads all records into memory.
   - Filters out the record matching the ID.
   - Rewrites remaining records to the file.

---

## 4. File Handling Explanation

### File Format (Pipe-Delimited `|`)
A single delimiter (`|`) separates fields. Unlike commas, pipes do not conflict with normal sentence text (such as in patient addresses or review comments).

Example row from `data/patients.txt`:
```
PAT-101|john_doe|pass123|John Doe|john.doe@example.com|0771234567|45 Galle Road, Colombo 03|VIP|Level:Gold;Discount:0.15;Priority:true
```

### Key Java I/O Classes Used:
1. `java.io.BufferedReader`: Efficiently buffers characters to provide seamless line-by-line reading via `readLine()`.
2. `java.io.BufferedWriter`: Buffers characters to write complete lines without excessive disk thrashing.
3. `java.io.FileReader` & `java.io.FileWriter`: Connects stream buffers to physical file handles on disk using UTF-8 encoding.
4. `java.nio.file.Files` & `java.nio.file.Path`: Used for directory creation (`Files.createDirectories()`) and path normalization.
5. `synchronized (FILE_LOCK)`: Ensures thread safety when multiple web requests write or read files concurrently.

---

## 5. Encapsulation Example

**Definition:** Bundling data (fields) and methods that operate on that data into a single unit, and restricting direct external access to prevent unintended tampering.

```java
public class Patient {
    // Encapsulation: Private member variables protect internal state
    private String patientId;
    private String name;
    private String email;

    // Public Getter - Controlled read access
    public String getEmail() {
        return email;
    }

    // Public Setter - Controlled write access with data validation
    public void setEmail(String email) {
        if (email != null && email.contains("@")) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}
```

---

## 6. Inheritance Example

**Definition:** Mechanism where a child class acquires the fields and behaviors of a parent class, promoting code reuse.

```java
// Parent / Superclass
public class Patient {
    private String patientId;
    private String name;
    // Common constructors, getters, setters
}

// Child / Subclass inherits all fields and methods from Patient
public class VIPPatient extends Patient {
    private String membershipLevel; // Specific to VIP
    private double discountRate;

    public VIPPatient(String patientId, String username, String password, String name,
                      String email, String phone, String address,
                      String membershipLevel, double discountRate, boolean priorityBooking) {
        // Calls parent constructor
        super(patientId, username, password, name, email, phone, address, "VIP");
        this.membershipLevel = membershipLevel;
        this.discountRate = discountRate;
    }
}
```

---

## 7. Polymorphism Example

**Definition:** The ability of an object to take many forms and execute different behaviors through the same method signature via dynamic method dispatch at runtime.

### Example: Consultation Billing Discount
```java
// In Base Class Patient:
public double calculateDiscount(double billAmount) {
    return 0.0; // Default: Regular patients receive no automatic discount
}

// In Subclass VIPPatient (Overridden):
@Override
public double calculateDiscount(double billAmount) {
    return billAmount * discountRate; // VIP patients receive percentage discount (e.g., 15%)
}

// Polymorphic Usage in PaymentService:
Patient patient = patientRepository.findById(patientId).get();
// At runtime, Java dynamically calls RegularPatient's or VIPPatient's version:
double discount = patient.calculateDiscount(doctorFee);
double finalBill = doctorFee - discount;
```

---

## 8. Abstraction Example

**Definition:** Hiding implementation details and exposing only essential interfaces and abstract contracts.

### Example: Doctor Availability Verification
```java
// 1. Interface declaring abstract contract
public interface DoctorAvailabilityChecker {
    boolean isDoctorAvailable(String doctorId, String date, String time);
    void validateDoctorAvailability(String doctorId, String date, String time, String excludeAptId);
}

// 2. Concrete implementation hidden behind the interface
@Service
public class DefaultDoctorAvailabilityChecker implements DoctorAvailabilityChecker {
    @Override
    public boolean isDoctorAvailable(String doctorId, String date, String time) {
        // Queries doctor repository and active appointments
        // Returns true if doctor is free and slot has no conflicts
    }
}

// 3. AppointmentService depends on the abstraction, not the concrete implementation:
@Service
public class AppointmentService {
    private final DoctorAvailabilityChecker availabilityChecker; // Interface reference

    public AppointmentService(DoctorAvailabilityChecker availabilityChecker) {
        this.availabilityChecker = availabilityChecker;
    }
}
```

---

## 9. Information Hiding Example

**Definition:** Preventing external classes from accessing or relying on internal implementation details.

1. **Private File Handler Locks:** The file synchronization lock `private static final Object FILE_LOCK` is hidden inside `FileHandler`. Outside services cannot tamper with the lock.
2. **Encapsulated Serialization:** `toFileString()` encapsulates the delimiter formatting. Controllers and services interact purely with typed Java objects (`Doctor`, `Patient`) and never touch pipe formatting directly.
3. **Password Protection:** Patient passwords are protected by encapsulation and never exposed openly in user profile queries.

---

## 10. How Appointment Booking Works (Step-by-Step)

```
1. Patient selects Doctor, Consultation Date, Time Slot, and Reason in UI.
   |
2. Frontend sends POST request to `/api/appointments/book`.
   |
3. Controller delegates to `AppointmentService.bookAppointment(appointment)`.
   |
4. Service validates required input fields (non-null, non-blank).
   |
5. Service verifies Patient existence via `PatientRepository.findById()`.
   |
6. Service calls `DoctorAvailabilityChecker.validateDoctorAvailability(doctorId, date, time)`.
   - Checks if doctor's status is 'Available'.
   - Queries `AppointmentRepository.findActiveByDoctorDateTime(doctorId, date, time)`.
   - If a non-cancelled appointment already exists, throws `ConflictException` (409 Conflict).
   |
7. Service auto-generates a unique Appointment ID (e.g., `APT-106`).
   |
8. Subclass is polymorphically instantiated (`EmergencyAppointment`, `FollowUpAppointment`, or `Appointment`).
   |
9. `AppointmentRepository.save()` appends record to `data/appointments.txt`.
   |
10. Success response (HTTP 200) returned to frontend with appointment details.
```

---

## 11. Frequently Asked Viva Questions & Model Answers

### Q1: Why did you use text files instead of a database like MySQL?
> **Answer:** "Our SE1020 coursework requires us to demonstrate raw Java File I/O techniques including `BufferedReader`, `BufferedWriter`, `FileReader`, and `FileWriter`. File storage allows us to showcase file stream management, record serialization, parsing, and atomic overwrite operations without abstracting them away behind an Object-Relational Mapping (ORM) framework or database engine."

### Q2: What happens if two patients book the same doctor at the exact same time?
> **Answer:** "The system prevents double-booking through the `DoctorAvailabilityChecker` abstraction. Before saving, `AppointmentService` queries `AppointmentRepository.findActiveByDoctorDateTime(doctorId, date, time)`. If an active (non-cancelled) booking exists for that doctor at that slot, a `ConflictException` is thrown, returning an informative HTTP 409 error message to the user."

### Q3: How do you handle file corruption or missing files?
> **Answer:** "In `FileHandler`, the method `ensureFileExists()` checks if parent directories and files exist on disk using `Files.createDirectories()` and `file.createNewFile()`. Additionally, during parsing, each repository surrounds line splitting with `try-catch` blocks; malformed or corrupted lines are gracefully skipped and logged without crashing the application."

### Q4: Can you point to where Polymorphism is used in your billing or payments?
> **Answer:** "Yes! First, in `Patient`, `calculateDiscount()` is overridden by `VIPPatient` to return a 15% discount, whereas `RegularPatient` returns $0. Second, the abstract class `Payment` has abstract methods `calculateProcessingFee()` and `getPaymentReceiptDetails()`. `CashPayment` calculates 0% processing fee, `CardPayment` calculates 1.5%, and `OnlinePayment` calculates 2.0%."

### Q5: How is Abstraction implemented?
> **Answer:** "We implemented abstraction in two ways:
> 1. Through abstract classes: `Staff` and `Payment` declare abstract methods (`getRoleDescription()`, `calculateDutyAllowance()`, `calculateProcessingFee()`) that enforce contracts on concrete subclasses.
> 2. Through interfaces: The `DoctorAvailabilityChecker` interface defines availability verification. `AppointmentService` depends strictly on this interface rather than a tight coupling to concrete doctor or repository details."

### Q6: How do you prevent thread concurrency issues when writing to files?
> **Answer:** "All write operations (`writeAllLines`, `appendLine`) and repository mutating methods use `synchronized` blocks and method signatures locking on a dedicated `FILE_LOCK` monitor object. This prevents race conditions where two simultaneous HTTP requests could interleave writes and corrupt the text files."

### Q7: What are the benefits of your 4-tier backend design?
> **Answer:** "Separation of concerns. Controllers only handle HTTP requests and JSON serialization; Services handle business rules, validation, and conflict checking; Repositories handle data persistence and querying; and FileHandler encapsulates raw Java I/O. This makes the codebase modular, testable, and maintainable."
