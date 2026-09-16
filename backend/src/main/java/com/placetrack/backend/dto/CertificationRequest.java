package com.placetrack.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** One certification on the student profile. */
public record CertificationRequest(
        @NotBlank(message = "Certification name is required")
        @Size(max = 150, message = "Certification name must be at most 150 characters")
        String certificationName,

        @NotBlank(message = "Issuing organization is required")
        @Size(max = 120, message = "Issuing organization must be at most 120 characters")
        String issuingOrganization,

        LocalDate issueDate,

        @Size(max = 300, message = "Credential link must be at most 300 characters")
        String credentialLink
) {}
