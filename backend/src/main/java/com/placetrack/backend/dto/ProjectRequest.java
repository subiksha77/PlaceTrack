package com.placetrack.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** One project on the student profile. */
public record ProjectRequest(
        @NotBlank(message = "Project name is required")
        @Size(max = 120, message = "Project name must be at most 120 characters")
        String projectName,

        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,

        @Size(max = 300, message = "Technologies must be at most 300 characters")
        String technologies,

        @Size(max = 300, message = "Project link must be at most 300 characters")
        String projectLink,

        @Size(max = 60, message = "Project role must be at most 60 characters")
        String projectRole
) {}
