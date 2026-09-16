package com.placetrack.backend.dto;

public record InterviewQuestionRequest(
        String category,
        String difficulty,
        String question,
        String answer,
        String explanation,
        String topic
) {}
