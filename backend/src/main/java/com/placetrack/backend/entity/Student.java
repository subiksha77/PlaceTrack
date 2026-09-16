package com.placetrack.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Student name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(name = "student_name", nullable = false)
    private String studentName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Department is required")
    @Column(name = "department", nullable = false)
    private String department;

    @NotNull(message = "Year is required")
    @Min(value = 1, message = "Year must be at least 1")
    @Max(value = 4, message = "Year must be at most 4")
    @Column(name = "year", nullable = false)
    private Integer year;

    @DecimalMin(value = "0.0", message = "CGPA must be at least 0.0")
    @DecimalMax(value = "10.0", message = "CGPA must be at most 10.0")
    @Column(name = "cgpa")
    private Double cgpa;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone must be a valid 10-digit Indian mobile number")
    @Column(name = "phone")
    private String phone;

    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;

    // ===== Personal + academic identity (placement profile) =====

    @Size(max = 30, message = "Register number must be at most 30 characters")
    @Column(name = "register_number", length = 30)
    private String registerNumber;

    @Size(max = 60, message = "Degree must be at most 60 characters")
    @Column(name = "degree", length = 60)
    private String degree;

    @Past(message = "Date of birth must be in the past")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Size(max = 20, message = "Gender must be at most 20 characters")
    @Column(name = "gender", length = 20)
    private String gender;

    @Size(max = 120, message = "Location must be at most 120 characters")
    @Column(name = "location", length = 120)
    private String location;

    @Size(max = 255, message = "Address must be at most 255 characters")
    @Column(name = "address", length = 255)
    private String address;

    @Size(max = 100, message = "City must be at most 100 characters")
    @Column(name = "city", length = 100)
    private String city;

    @Size(max = 100, message = "State must be at most 100 characters")
    @Column(name = "state", length = 100)
    private String state;

    @Size(max = 255, message = "LinkedIn URL must be at most 255 characters")
    @Column(name = "linkedin", length = 255)
    private String linkedIn;

    @Size(max = 255, message = "GitHub URL must be at most 255 characters")
    @Column(name = "github", length = 255)
    private String gitHub;

    @Size(max = 500, message = "Coding profiles must be at most 500 characters")
    @Column(name = "coding_profiles", length = 500)
    private String codingProfiles;

    @Column(name = "achievements", columnDefinition = "TEXT")
    private String achievements;

    @Enumerated(EnumType.STRING)
    @Column(name = "placement_status", nullable = false)
    private PlacementStatus placementStatus = PlacementStatus.NOT_PLACED;

    // ===== Placement profile (Phase A) =====

    /** Linked login account (users.id). Null for students created by the placement office. */
    @Column(name = "user_id", unique = true)
    private Long userId;

    @Min(value = 2000, message = "Graduation year must be 2000 or later")
    @Max(value = 2100, message = "Graduation year must be 2100 or earlier")
    @Column(name = "graduation_year")
    private Integer graduationYear;

    @DecimalMin(value = "0.0", message = "10th percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "10th percentage must be at most 100")
    @Column(name = "tenth_percentage")
    private Double tenthPercentage;

    @DecimalMin(value = "0.0", message = "12th percentage must be at least 0")
    @DecimalMax(value = "100.0", message = "12th percentage must be at most 100")
    @Column(name = "twelfth_percentage")
    private Double twelfthPercentage;

    @Min(value = 0, message = "Backlogs cannot be negative")
    @Column(name = "backlogs")
    private Integer backlogs;

    @Column(name = "resume_file_name", length = 255)
    private String resumeFileName;

    /** Server-side storage path - never exposed raw to clients. */
    @Column(name = "resume_path", length = 512)
    private String resumePath;

    @Column(name = "resume_updated_at")
    private LocalDateTime resumeUpdatedAt;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Placement> placements;

    public enum PlacementStatus {
        PLACED, NOT_PLACED
    }
}
