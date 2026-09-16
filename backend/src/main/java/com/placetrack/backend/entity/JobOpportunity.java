package com.placetrack.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Job opportunity created for a company in PlaceTrack.
 *
 * One company can have multiple opportunities (different roles, locations,
 * packages, drive dates). This is the canonical requirements record: minimum
 * CGPA, eligible departments, eligible year, graduation year, backlogs allowed,
 * required/preferred skills, package, drive date, deadline, selection process,
 * status.
 */
@Entity
@Table(name = "job_opportunities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobOpportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @NotBlank(message = "Job role is required")
    @Size(max = 200, message = "Job role must be at most 200 characters")
    @Column(nullable = false, length = 200)
    private String jobRole;

    @Column(columnDefinition = "TEXT")
    private String description;

    @DecimalMin(value = "0.0", message = "Package cannot be negative")
    @DecimalMax(value = "1000.0", message = "Package appears unusually high")
    @Column(name = "package_lpa")
    private Double packageLpa;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", length = 30)
    private JobType jobType = JobType.FULL_TIME;

    @Size(max = 200, message = "Work location must be at most 200 characters")
    @Column(name = "work_location", length = 200)
    private String workLocation;

    @DecimalMin(value = "0.0", message = "Minimum CGPA cannot be negative")
    @DecimalMax(value = "10.0", message = "Minimum CGPA must be at most 10.0")
    @Column(name = "min_cgpa")
    private Double minCgpa;

    @Size(max = 200, message = "Eligible departments must be at most 200 characters")
    @Column(name = "eligible_departments", length = 200)
    private String eligibleDepartments;

    @Min(value = 1, message = "Eligible year must be at least 1")
    @Max(value = 2100, message = "Eligible year must be at most 2100")
    @Column(name = "eligible_year")
    private Integer eligibleYear;

    @Min(value = 1900, message = "Graduation year must be at least 1900")
    @Max(value = 2100, message = "Graduation year must be at most 2100")
    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Min(value = 0, message = "Backlogs allowed cannot be negative")
    @Max(value = 20, message = "Backlogs allowed appears unusually high")
    @Column(name = "backlogs_allowed")
    private Integer backlogsAllowed;

    @Size(max = 300, message = "Required skills must be at most 300 characters")
    @Column(name = "required_skills", length = 300)
    private String requiredSkills;

    @Size(max = 300, message = "Preferred skills must be at most 300 characters")
    @Column(name = "preferred_skills", length = 300)
    private String preferredSkills;

    @Column(name = "drive_date")
    private LocalDate driveDate;

    @Column(name = "application_deadline")
    private LocalDate applicationDeadline;

    @Size(max = 500, message = "Selection process must be at most 500 characters")
    @Column(name = "selection_process", length = 500)
    private String selectionProcess;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private OpportunityStatus status = OpportunityStatus.OPEN;

    public enum JobType {
        FULL_TIME, INTERNSHIP, CONTRACT, CONTRACT_INTERNSHIP, WFH, OTHER
    }

    public enum OpportunityStatus {
        OPEN, CLOSED, CANCELLED, COMPLETED
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobOpportunity that = (JobOpportunity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

