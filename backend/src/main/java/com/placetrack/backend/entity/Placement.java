package com.placetrack.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "placements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Placement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Student is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull(message = "Company is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @NotBlank(message = "Job role is required")
    @Column(name = "job_role", nullable = false)
    private String jobRole;

    @NotNull(message = "Package (LPA) is required")
    @DecimalMin(value = "0.0", message = "Package cannot be negative")
    @Column(name = "package_lpa", nullable = false)
    private Double packageLpa;

    @NotNull(message = "Placement date is required")
    @Column(name = "placement_date", nullable = false)
    private LocalDate placementDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PlacementStatus status = PlacementStatus.SELECTED;

    public enum PlacementStatus {
        SELECTED, REJECTED, PENDING
    }
}
