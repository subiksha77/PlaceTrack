package com.placetrack.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** One internship on the student profile. */
public record InternshipRequest(
        @NotBlank(message = "Company name is required")
        @Size(max = 120, message = "Company name must be at most 120 characters")
        String companyName,

        @Size(max = 120, message = "Role must be at most 120 characters")
        @NotBlank(message = "Role is required")
        String role,

        @NotBlank(message = "Duration is required")
        @Size(max = 60, message = "Duration must be at most 60 characters")
        String duration,

        @Size(max = 1000, message = "Description must be at most 1000 characters")
        String description
) {}
