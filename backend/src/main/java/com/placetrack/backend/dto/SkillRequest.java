package com.placetrack.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** One technical skill on the student profile. */
public record SkillRequest(
        @NotBlank(message = "Skill name is required")
        @Size(max = 60, message = "Skill name must be at most 60 characters")
        String skillName,

        @Pattern(regexp = "(?i)^(PROGRAMMING|FRAMEWORK|DATABASE|TOOL|OTHER)$",
                message = "Category must be PROGRAMMING, FRAMEWORK, DATABASE, TOOL or OTHER")
        String category
) {}
