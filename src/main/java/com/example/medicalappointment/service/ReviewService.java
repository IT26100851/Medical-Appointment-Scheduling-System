package com.example.medicalappointment.service;

import com.example.medicalappointment.exception.ResourceNotFoundException;
import com.example.medicalappointment.exception.ValidationException;
import com.example.medicalappointment.model.PatientReview;
import com.example.medicalappointment.model.Review;
import com.example.medicalappointment.model.VerifiedReview;
import com.example.medicalappointment.repository.DoctorRepository;
import com.example.medicalappointment.repository.PatientRepository;
import com.example.medicalappointment.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ReviewService
 * Demonstrates:
 * - CRUD operations for Patient feedback and doctor ratings
 * - Polymorphic instantiation of PatientReview and VerifiedReview
 * - File storage in data/reviews.txt
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         DoctorRepository doctorRepository,
                         PatientRepository patientRepository) {
        this.reviewRepository = reviewRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    // ==========================================
    // CRUD - Create / Submit Review
    // ==========================================
    public Review submitReview(Review review) {
        validateReview(review);

        if (doctorRepository.findById(review.getDoctorId()).isEmpty()) {
            throw new ResourceNotFoundException("Doctor not found with ID: " + review.getDoctorId());
        }

        if (patientRepository.findById(review.getPatientId()).isEmpty()) {
            throw new ResourceNotFoundException("Patient not found with ID: " + review.getPatientId());
        }

        if (review.getReviewId() == null || review.getReviewId().isBlank()) {
            review.setReviewId("REV-" + (100 + reviewRepository.findAll().size() + 1));
        }

        if (review.getDate() == null || review.getDate().isBlank()) {
            review.setDate(LocalDate.now().toString());
        }

        // Polymorphic creation based on review type
        Review toSave = review;
        if ("VerifiedReview".equalsIgnoreCase(review.getReviewType()) && !(review instanceof VerifiedReview)) {
            toSave = new VerifiedReview(review.getReviewId(), review.getPatientId(), review.getDoctorId(),
                    review.getRating(), review.getComment(), review.getDate(),
                    "APT-" + review.getPatientId(), "Clinical Treatment");
        } else if (!(review instanceof PatientReview) && !(review instanceof VerifiedReview)) {
            toSave = new PatientReview(review.getReviewId(), review.getPatientId(), review.getDoctorId(),
                    review.getRating(), review.getComment(), review.getDate());
        }

        return reviewRepository.save(toSave);
    }

    // ==========================================
    // CRUD - Read (Get all reviews)
    // ==========================================
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    // ==========================================
    // CRUD - Read (Get review by ID)
    // ==========================================
    public Review getReviewById(String reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));
    }

    // ==========================================
    // Query - Get reviews by Doctor ID
    // ==========================================
    public List<Review> getReviewsByDoctor(String doctorId) {
        return reviewRepository.findByDoctorId(doctorId);
    }

    // ==========================================
    // Query - Get reviews by Patient ID
    // ==========================================
    public List<Review> getReviewsByPatient(String patientId) {
        return reviewRepository.findByPatientId(patientId);
    }

    // ==========================================
    // Search - Find reviews by keyword
    // ==========================================
    public List<Review> searchReviews(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllReviews();
        }
        String lower = keyword.toLowerCase().trim();
        return getAllReviews().stream()
                .filter(r -> r.getReviewId().toLowerCase().contains(lower)
                        || r.getDoctorId().toLowerCase().contains(lower)
                        || r.getPatientId().toLowerCase().contains(lower)
                        || (r.getComment() != null && r.getComment().toLowerCase().contains(lower)))
                .collect(Collectors.toList());
    }

    // ==========================================
    // CRUD - Update (Update review comment and rating)
    // ==========================================
    public Review updateReview(String reviewId, Review updated) {
        Review existing = getReviewById(reviewId);

        if (updated.getRating() >= 1 && updated.getRating() <= 5) {
            existing.setRating(updated.getRating());
        }
        if (updated.getComment() != null && !updated.getComment().isBlank()) {
            existing.setComment(updated.getComment().trim());
        }

        return reviewRepository.update(existing);
    }

    // ==========================================
    // CRUD - Delete (Delete review)
    // ==========================================
    public boolean deleteReview(String reviewId) {
        getReviewById(reviewId);
        return reviewRepository.deleteById(reviewId);
    }

    private void validateReview(Review review) {
        if (review.getDoctorId() == null || review.getDoctorId().isBlank()) {
            throw new ValidationException("Doctor ID is required.");
        }
        if (review.getPatientId() == null || review.getPatientId().isBlank()) {
            throw new ValidationException("Patient ID is required.");
        }
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new ValidationException("Rating must be between 1 and 5.");
        }
        if (review.getComment() == null || review.getComment().isBlank()) {
            throw new ValidationException("Review comment is required.");
        }
    }
}
