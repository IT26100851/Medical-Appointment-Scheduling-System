package com.example.medicalappointment.repository;

import com.example.medicalappointment.model.PatientReview;
import com.example.medicalappointment.model.Review;
import com.example.medicalappointment.model.VerifiedReview;
import com.example.medicalappointment.util.FileHandler;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ReviewRepository
 * Demonstrates:
 * - CRUD operations for Review entity using File I/O
 * - Polymorphic instantiation of PatientReview and VerifiedReview subclasses
 * - File read/write against 'data/reviews.txt'
 */
@Repository
public class ReviewRepository {

    private static final String FILE_PATH = "data/reviews.txt";

    // ==========================================
    // CRUD - Read (Find all reviews)
    // ==========================================
    public List<Review> findAll() {
        List<String> lines = FileHandler.readAllLines(FILE_PATH);
        List<Review> reviews = new ArrayList<>();

        for (String line : lines) {
            try {
                String[] parts = FileHandler.splitLine(line);
                if (parts.length >= 7) {
                    String reviewId = parts[0].trim();
                    String patientId = parts[1].trim();
                    String doctorId = parts[2].trim();
                    int rating = Integer.parseInt(parts[3].trim());
                    String comment = parts[4].trim();
                    String date = parts[5].trim();
                    String type = parts[6].trim();
                    String extra = (parts.length > 7) ? parts[7].trim() : "";

                    // Polymorphism & Inheritance
                    if ("VerifiedReview".equalsIgnoreCase(type)) {
                        String aptId = "";
                        String treatment = "Specialist Treatment";
                        if (!extra.isEmpty() && extra.contains(";")) {
                            for (String token : extra.split(";")) {
                                if (token.startsWith("Apt:")) aptId = token.substring(4);
                                else if (token.startsWith("Treatment:")) treatment = token.substring(10);
                            }
                        }
                        reviews.add(new VerifiedReview(reviewId, patientId, doctorId, rating, comment, date, aptId, treatment));
                    } else {
                        reviews.add(new PatientReview(reviewId, patientId, doctorId, rating, comment, date));
                    }
                }
            } catch (Exception ex) {
                System.err.println("[ReviewRepository] Skipping invalid record: " + line + " - " + ex.getMessage());
            }
        }
        return reviews;
    }

    // ==========================================
    // CRUD - Read (Find review by ID)
    // ==========================================
    public Optional<Review> findById(String reviewId) {
        if (reviewId == null) return Optional.empty();
        return findAll().stream()
                .filter(r -> reviewId.equalsIgnoreCase(r.getReviewId()))
                .findFirst();
    }

    // ==========================================
    // Query - Find reviews by Doctor ID
    // ==========================================
    public List<Review> findByDoctorId(String doctorId) {
        if (doctorId == null) return new ArrayList<>();
        return findAll().stream()
                .filter(r -> doctorId.equalsIgnoreCase(r.getDoctorId()))
                .collect(Collectors.toList());
    }

    // ==========================================
    // Query - Find reviews by Patient ID
    // ==========================================
    public List<Review> findByPatientId(String patientId) {
        if (patientId == null) return new ArrayList<>();
        return findAll().stream()
                .filter(r -> patientId.equalsIgnoreCase(r.getPatientId()))
                .collect(Collectors.toList());
    }

    // ==========================================
    // CRUD - Create (Save new review)
    // ==========================================
    public synchronized Review save(Review review) {
        List<Review> existing = findAll();
        boolean exists = existing.stream().anyMatch(r -> r.getReviewId().equalsIgnoreCase(review.getReviewId()));
        if (exists) {
            return update(review);
        }

        FileHandler.appendLine(FILE_PATH, review.toFileString());
        return review;
    }

    // ==========================================
    // CRUD - Update (Update existing review)
    // ==========================================
    public synchronized Review update(Review updatedReview) {
        List<Review> all = findAll();
        List<String> newLines = new ArrayList<>();
        boolean found = false;

        for (Review r : all) {
            if (r.getReviewId().equalsIgnoreCase(updatedReview.getReviewId())) {
                newLines.add(updatedReview.toFileString());
                found = true;
            } else {
                newLines.add(r.toFileString());
            }
        }

        if (found) {
            FileHandler.writeAllLines(FILE_PATH, newLines);
            return updatedReview;
        } else {
            return save(updatedReview);
        }
    }

    // ==========================================
    // CRUD - Delete (Delete review by ID)
    // ==========================================
    public synchronized boolean deleteById(String reviewId) {
        List<Review> all = findAll();
        List<String> remainingLines = new ArrayList<>();
        boolean removed = false;

        for (Review r : all) {
            if (r.getReviewId().equalsIgnoreCase(reviewId)) {
                removed = true;
            } else {
                remainingLines.add(r.toFileString());
            }
        }

        if (removed) {
            FileHandler.writeAllLines(FILE_PATH, remainingLines);
        }
        return removed;
    }
}
