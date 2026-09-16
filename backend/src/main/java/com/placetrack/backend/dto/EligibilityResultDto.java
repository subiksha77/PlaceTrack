package com.placetrack.backend.dto;

import java.util.List;

public record EligibilityResultDto(
        Long jobId,
        String jobRole,
        String companyName,
        boolean eligible,
        String summary,
        List<Criterion> criteria,
        List<String> matchedSkills,
        List<String> missingSkills
) {
    public record Criterion(
            String criterionName,
            boolean passed,
            String requiredValue,
            String actualValue,
            String details
    ) {}
}
