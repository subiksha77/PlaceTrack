package com.placetrack.backend.dto;

import jakarta.validation.constraints.*;

/**
 * Incoming payload for account registration.
 * The raw password is used only to generate a BCrypt hash and is never stored or returned.
 */
public record RegisterRequest(

        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String fullName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
        String password,

        @NotBlank(message = "Department is required")
        String department,

        @NotNull(message = "Year is required")
        @Min(value = 1, message = "Year must be at least 1")
        @Max(value = 4, message = "Year must be at most 4")
        Integer year,

        /** Optional: "STUDENT" (default) or "ADMIN" (placement officer). */
        String role
) {}
