package com.placetrack.backend.dto;

public record ApplicationResponse(
        Long id,
        Long studentId,
        String studentName,
        Long companyId,
        String companyName,
        Long jobOpportunityId,
        String jobRole,
        Double packageLpa,
        String appliedDate,
        String status,
        String resumeFileName,
        String notes,
        String statusUpdatedAt
) {}
