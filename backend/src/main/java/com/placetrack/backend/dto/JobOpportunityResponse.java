package com.placetrack.backend.dto;

import java.util.List;

public record JobOpportunityResponse(
        Long id,
        Long companyId,
        String companyName,
        String jobRole,
        String jobDescription,
        Double packageLpa,
        String jobType,
        String workLocation,
        Double minCgpa,
        String eligibleDepartments,
        String eligibleYear,
        String graduationYear,
        Integer allowedBacklogs,
        String requiredSkills,
        String preferredSkills,
        String driveDate,
        String applicationDeadline,
        String selectionProcess,
        String status,
        List<PlacementDriveResponse> drives
) {}