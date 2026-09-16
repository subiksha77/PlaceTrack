package com.placetrack.backend.controller;

import com.placetrack.backend.dto.JobRecommendationDto;
import com.placetrack.backend.service.EligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final EligibilityService eligibilityService;

    // GET /api/recommendations - Recommended jobs for currently logged-in student
    @GetMapping
    public ResponseEntity<List<JobRecommendationDto>> getMyRecommendations(
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        return ResponseEntity.ok(eligibilityService.getRecommendationsForUser(token));
    }

    // GET /api/recommendations/student/{studentId} - For admin view
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<JobRecommendationDto>> getStudentRecommendations(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(eligibilityService.getRecommendations(studentId));
    }
}
