package com.placetrack.backend.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record JobOpportunityRequest(
        @NotNull(message = "Company is required")
        Long companyId,

        @NotBlank(message = "Job role is required")
        String jobRole,

        String jobDescription,

        @DecimalMin(value = "0.0", message = "Package must be at least 0")
        Double packageLpa,

        String jobType,

        String workLocation,

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

        String driveDate,

        String applicationDeadline,

        String selectionProcess,

        String status
) {}