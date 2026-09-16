package com.placetrack.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A student's application to a job opportunity (and therefore to the company
 * that owns it).
 *
 * The status column drives the placement journey shown in the UI:
 * APPLIED -> SHORTLISTED -> ASSESSMENT -> INTERVIEW -> SELECTED / REJECTED.
 *
 * A student can apply to the same opportunity only once - enforced both in the
 * service layer (clear error message) and by a DB unique constraint.
 */
@Entity
@Table(name = "applications",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_application_student_job",
                columnNames = {"student_id", "job_opportunity_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Student is required")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /** Company that owns the opportunity - kept denormalised so reports never need a join. */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "job_opportunity_id", nullable = false)
    private JobOpportunity jobOpportunity;

    @Column(name = "applied_date", nullable = false)
    private LocalDate appliedDate = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Size(max = 255, message = "Resume file name must be at most 255 characters")
    @Column(name = "resume_file_name", length = 255)
    private String resumeFileName;

    @Size(max = 1000, message = "Notes must be at most 1000 characters")
    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "status_updated_at")
    private LocalDateTime statusUpdatedAt;

    /** Application lifecycle. Order matters - the UI renders it as a journey. */
    public enum ApplicationStatus {
        APPLIED, SHORTLISTED, ASSESSMENT, INTERVIEW, SELECTED, REJECTED, WITHDRAWN;

        /** Lenient parser used for request payloads; unknown values fall back to APPLIED. */
        public static ApplicationStatus from(String value) {
            if (value == null || value.isBlank()) {
                return APPLIED;
            }
            try {
                return ApplicationStatus.valueOf(value.trim().toUpperCase().replace(' ', '_'));
            } catch (IllegalArgumentException ex) {
                return APPLIED;
            }
        }

        /** True once the application has reached a final outcome. */
        public boolean isFinal() {
            return this == SELECTED || this == REJECTED;
        }
    }
}
