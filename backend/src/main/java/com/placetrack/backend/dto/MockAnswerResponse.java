package com.placetrack.backend.dto;

public record MockAnswerResponse(
        Long id,
        Integer questionIndex,
        String questionText,
        String category,
        String difficulty,
        String studentAnswer,
        String correctAnswer,
        Integer score,
        String feedback
) {}
