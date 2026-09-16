package com.placetrack.backend.dto;

import java.util.List;

public record MockInterviewResponse(
        Long id,
        Long studentId,
        String studentName,
        String targetCompany,
        String targetRole,
        String interviewType,
        String difficulty,
        String status,
        Integer totalQuestions,
        Integer score,
        String startedAt,
        String completedAt,
        String strengths,
        String improvements,
        String suggestedTopics,
        List<MockAnswerResponse> answers
) {}
