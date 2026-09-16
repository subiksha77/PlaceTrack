package com.placetrack.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/** A certification on a student's placement profile. */
@Entity
@Table(name = "student_certifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCertification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore
    private Student student;

    @NotBlank(message = "Certification name is required")
    @Size(max = 150, message = "Certification name must be at most 150 characters")
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @NotBlank(message = "Issuing organization is required")
    @Size(max = 120, message = "Organization must be at most 120 characters")
    @Column(name = "organization", nullable = false, length = 120)
    private String organization;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Size(max = 300, message = "Credential link must be at most 300 characters")
    @Column(name = "credential_link", length = 300)
    private String credentialLink;
}
