package com.placetrack.backend.controller;

import com.placetrack.backend.dto.JobOpportunityRequest;
import com.placetrack.backend.dto.JobOpportunityResponse;
import com.placetrack.backend.service.JobOpportunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobOpportunityController {

    private final JobOpportunityService jobService;

    @GetMapping
    public ResponseEntity<List<JobOpportunityResponse>> getAllJobs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(jobService.findAll(search, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobOpportunityResponse> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.findById(id));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<JobOpportunityResponse>> getJobsByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(jobService.findByCompanyId(companyId));
    }

    @PostMapping
    public ResponseEntity<JobOpportunityResponse> createJob(@Valid @RequestBody JobOpportunityRequest request) {
        JobOpportunityResponse created = jobService.create(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobOpportunityResponse> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobOpportunityRequest request) {
        return ResponseEntity.ok(jobService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteJob(@PathVariable Long id) {
        jobService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Job opportunity deleted successfully"));
    }
}
