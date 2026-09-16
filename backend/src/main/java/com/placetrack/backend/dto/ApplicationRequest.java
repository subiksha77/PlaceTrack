package com.placetrack.backend.dto;

public record ApplicationRequest(
        Long jobOpportunityId,
        String notes
) {}
