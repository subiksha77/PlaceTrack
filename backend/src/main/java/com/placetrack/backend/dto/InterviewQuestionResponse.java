package com.placetrack.backend.dto;

public record InterviewQuestionResponse(
        Long id,
        String category,
        String categoryLabel,
        String difficulty,
        String question,
        String answer,
        String explanation,
        String topic,
        boolean active
) {}
