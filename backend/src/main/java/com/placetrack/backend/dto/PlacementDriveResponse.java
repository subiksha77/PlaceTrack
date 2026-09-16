package com.placetrack.backend.dto;

import java.util.List;

public record PlacementDriveResponse(
        Long id,
        Long companyId,
        String companyName,
        Long jobOpportunityId,
        String jobRole,
        Double packageLpa,
        Double minCgpa,
        String eligibleDepartments,
        String eligibleYear,
        String graduationYear,
        Integer allowedBacklogs,
        String requiredSkills,
        String preferredSkills,
        String driveDate,
        String applicationDeadline,
        String workLocation,
        String selectionProcess,
        String status
) {}