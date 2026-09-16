package com.placetrack.backend.dto;

public record MockAnswerRequest(
        Integer questionIndex,
        String studentAnswer
) {}
