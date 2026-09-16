package com.placetrack.backend.controller;

import com.placetrack.backend.dto.EligibilityResultDto;
import com.placetrack.backend.service.EligibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eligibility")
@RequiredArgsConstructor
public class EligibilityController {

    private final EligibilityService eligibilityService;

    // GET /api/eligibility/check/{jobId} - Check eligibility of current logged-in student
    @GetMapping("/check/{jobId}")
    public ResponseEntity<EligibilityResultDto> checkMyEligibility(
            @PathVariable Long jobId,
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        return ResponseEntity.ok(eligibilityService.checkEligibilityForUser(token, jobId));
    }

    // GET /api/eligibility/student/{studentId}/job/{jobId} - Admin check
    @GetMapping("/student/{studentId}/job/{jobId}")
    public ResponseEntity<EligibilityResultDto> checkStudentEligibility(
            @PathVariable Long studentId,
            @PathVariable Long jobId) {
        return ResponseEntity.ok(eligibilityService.checkEligibility(studentId, jobId));
    }
}
