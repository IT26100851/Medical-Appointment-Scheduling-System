package com.example.medicalappointment.controller;

import com.example.medicalappointment.model.ApiResponse;
import com.example.medicalappointment.model.PatientReview;
import com.example.medicalappointment.model.Review;
import com.example.medicalappointment.model.VerifiedReview;
import com.example.medicalappointment.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ReviewController
 * REST endpoints for Feedback and Review Management
 * Demonstrates CRUD operations and polymorphic reviews (PatientReview, VerifiedReview)
 */
@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // CRUD - Create / Submit Review
    @PostMapping
    public ResponseEntity<ApiResponse<Review>> submitReview(@RequestBody Map<String, Object> body) {
        String patientId = (String) body.get("patientId");
        String doctorId = (String) body.get("doctorId");
        int rating = body.get("rating") != null ? Integer.parseInt(body.get("rating").toString()) : 5;
        String comment = (String) body.get("comment");
        String reviewType = (String) body.getOrDefault("reviewType", "PatientReview");

        Review review;
        if ("VerifiedReview".equalsIgnoreCase(reviewType)) {
            String aptId = (String) body.getOrDefault("appointmentId", "APT-101");
            String treatment = (String) body.getOrDefault("treatmentTag", "Clinical Consultation");
            review = new VerifiedReview(null, patientId, doctorId, rating, comment, null, aptId, treatment);
        } else {
            review = new PatientReview(null, patientId, doctorId, rating, comment, null);
        }

        Review saved = reviewService.submitReview(review);
        return ResponseEntity.ok(ApiResponse.ok("Review submitted successfully!", saved));
    }

    // CRUD - Read (Get all reviews or search)
    @GetMapping
    public ResponseEntity<ApiResponse<List<Review>>> getAllReviews(
            @RequestParam(required = false) String search) {
        List<Review> list = (search != null && !search.isBlank())
                ? reviewService.searchReviews(search)
                : reviewService.getAllReviews();
        return ResponseEntity.ok(ApiResponse.ok("Reviews retrieved successfully", list));
    }

    // CRUD - Read (Get review by ID)
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Review>> getReviewById(@PathVariable String reviewId) {
        Review review = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(ApiResponse.ok("Review found", review));
    }

    // Query - Get reviews by Doctor ID
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<ApiResponse<List<Review>>> getReviewsByDoctor(@PathVariable String doctorId) {
        List<Review> list = reviewService.getReviewsByDoctor(doctorId);
        return ResponseEntity.ok(ApiResponse.ok("Doctor reviews retrieved", list));
    }

    // Query - Get reviews by Patient ID
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<Review>>> getReviewsByPatient(@PathVariable String patientId) {
        List<Review> list = reviewService.getReviewsByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.ok("Patient reviews retrieved", list));
    }

    // CRUD - Update (Update review comment or rating)
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Review>> updateReview(
            @PathVariable String reviewId,
            @RequestBody Review review) {
        Review updated = reviewService.updateReview(reviewId, review);
        return ResponseEntity.ok(ApiResponse.ok("Review updated successfully!", updated));
    }

    // CRUD - Delete (Delete review)
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable String reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.ok("Review deleted successfully"));
    }
}
