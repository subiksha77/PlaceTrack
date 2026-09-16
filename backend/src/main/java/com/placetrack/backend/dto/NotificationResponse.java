package com.placetrack.backend.dto;

public record NotificationResponse(
        Long id,
        Long userId,
        String title,
        String message,
        String type,
        String link,
        boolean read,
        String createdAt
) {}
