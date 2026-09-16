package com.placetrack.backend.dto;

public record MockInterviewStartRequest(
        String interviewType,
        String difficulty,
        String targetCompany,
        String targetRole,
        Integer questionCount
) {}
