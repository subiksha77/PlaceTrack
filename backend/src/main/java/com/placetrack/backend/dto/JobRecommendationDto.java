package com.placetrack.backend.dto;

import java.util.List;

public record JobRecommendationDto(
        Long jobId,
        String jobRole,
        Long companyId,
        String companyName,
        Double packageLpa,
        String location,
        int matchPercentage,
        boolean eligible,
        List<String> matchingSkills,
        List<String> missingSkills,
        String reason
) {}
