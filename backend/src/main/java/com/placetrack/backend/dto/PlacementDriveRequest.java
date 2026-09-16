package com.placetrack.backend.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record PlacementDriveRequest(
        @NotNull(message = "Company is required")
        Long companyId,

        @NotNull(message = "Job opportunity is required")
        Long jobOpportunityId,

        String jobRole,

        @DecimalMin(value = "0.0", message = "Package must be at least 0")
        Double packageLpa,

        @DecimalMin(value = "0.0", message = "Minimum CGPA must be at least 0")
        @DecimalMax(value = "10.0", message = "Minimum CGPA must be at most 10")
        Double minCgpa,

        String eligibleDepartments,

        String eligibleYear,

        String graduationYear,

        @Min(value = 0, message = "Allowed backlogs cannot be negative")
        Integer allowedBacklogs,

        String requiredSkills,

        String preferredSkills,

        @NotBlank(message = "Drive date is required")
        String driveDate,

        @NotBlank(message = "Application deadline is required")
        String applicationDeadline,

        String workLocation,

        String selectionProcess,

        String status
) {}