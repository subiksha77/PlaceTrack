package com.placetrack.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * A placement drive on the company/college calendar.
 *
 * Drives can be linked directly to a company, or to a specific job opportunity
 * when you want the drive to carry one particular role. Either field may be
 * null if you just want a calendar entry, but normally you link at least the
 * company.
 */
@Entity
@Table(name = "placement_drives")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlacementDrive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_opportunity_id")
    private JobOpportunity jobOpportunity;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "package_lpa")
    private Double packageLpa;

    @Column(name = "min_cgpa")
    private Double minCgpa;

    @Column(name = "eligible_departments", length = 200)
    private String eligibleDepartments;

    @Column(name = "eligible_year")
    private Integer eligibleYear;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Column(name = "backlogs_allowed")
    private Integer backlogsAllowed;

    @Column(name = "required_skills", length = 300)
    private String requiredSkills;

    @Column(name = "preferred_skills", length = 300)
    private String preferredSkills;

    @Column(name = "drive_date")
    private LocalDate driveDate;

    @Column(name = "application_deadline")
    private LocalDate applicationDeadline;

    @Column(name = "drive_location", length = 200)
    private String driveLocation;

    @Column(name = "selection_process", length = 500)
    private String selectionProcess;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private Status status = Status.UPCOMING;

    /**
     * Drive lifecycle statuses. The default is UPCOMING. Once the actual
     * drive date arrives, the placement cell typically moves it to OPEN; when
     * recruitment ends, it becomes CLOSED/ONBOARDING; after offers are
     * extended and joined, COMPLETED.
     */
    public enum Status {
        UPCOMING, OPEN, CLOSED, COMPLETED, ONBOARDING
    }
}
