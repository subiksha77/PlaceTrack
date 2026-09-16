package com.placetrack.backend.dto;

public record NotificationRequest(
        Long userId,
        String title,
        String message,
        String type,
        String link
) {}
