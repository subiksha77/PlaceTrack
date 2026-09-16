package com.placetrack.backend.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

/** Update of the logged-in student's own placement profile basics. */
public record ProfileUpdateRequest(
        @NotBlank(message = "Full name is required")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String studentName,

        @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone must be a valid 10-digit Indian mobile number")
        String phone,

        @Size(max = 500, message = "Skills summary must be at most 500 characters")
        String skills,

        @DecimalMin(value = "0.0", message = "CGPA must be at least 0.0")
        @DecimalMax(value = "10.0", message = "CGPA must be at most 10.0")
        Double cgpa,

        @Min(value = 2000, message = "Graduation year must be 2000 or later")
        @Max(value = 2100, message = "Graduation year must be 2100 or earlier")
        Integer graduationYear,

        @DecimalMin(value = "0.0", message = "10th percentage must be at least 0")
        @DecimalMax(value = "100.0", message = "10th percentage must be at most 100")
        Double tenthPercentage,

        @DecimalMin(value = "0.0", message = "12th percentage must be at least 0")
        @DecimalMax(value = "100.0", message = "12th percentage must be at most 100")
        Double twelfthPercentage,

        @Min(value = 0, message = "Backlogs cannot be negative")
        Integer backlogs,

        @Size(max = 30, message = "Register number must be at most 30 characters")
        String registerNumber,

        @Size(max = 60, message = "Degree must be at most 60 characters")
        String degree,

        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth,

        @Pattern(regexp = "(?i)^(MALE|FEMALE|OTHER|)$", message = "Gender must be Male, Female or Other")
        String gender,

        @Size(max = 120, message = "Location must be at most 120 characters")
        String location,

        @Size(max = 255, message = "Address must be at most 255 characters")
        String address,

        @Size(max = 100, message = "City must be at most 100 characters")
        String city,

        @Size(max = 100, message = "State must be at most 100 characters")
        String state,

        @Size(max = 255, message = "LinkedIn URL must be at most 255 characters")
        String linkedIn,

        @Size(max = 255, message = "GitHub URL must be at most 255 characters")
        String gitHub,

        @Size(max = 500, message = "Coding profiles must be at most 500 characters")
        String codingProfiles,

        String achievements
) {}
