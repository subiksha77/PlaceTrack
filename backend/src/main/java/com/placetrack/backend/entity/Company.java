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
 * Company directory record for PlaceTrack.
 *
 * The directory maintains the company's public-facing info plus a main
 * recruitment context (role, package, eligibility, drive date, deadline,
 * location, status, eligibility summary). Where a company has multiple
 * distinct roles/drives, those are modelled on {@link JobOpportunity} and
 * {@link PlacementDrive} linked to the same company.
 */
@Entity
@Table(name = "companies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 200, message = "Company name must be between 2 and 200 characters")
    @Column(nullable = false, length = 200)
    private String name;

    @Size(max = 100, message = "Industry must be at most 100 characters")
    @Column(length = 100)
    private String industry;

    @Size(max = 255, message = "Website must be at most 255 characters")
    @Column(length = 255)
    private String website;

    @NotBlank(message = "Location is required")
    @Size(max = 150, message = "Location must be at most 150 characters")
    @Column(nullable = false, length = 150)
    private String location;

    @Size(max = 150, message = "Headquarters must be at most 150 characters")
    @Column(name = "headquarters", length = 150)
    private String headquarters;

    @Size(max = 50, message = "Company size must be at most 50 characters")
    @Column(name = "company_size", length = 50)
    private String companySize;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(columnDefinition = "TEXT")
    private String description;

    // ---- Recruitment / drive context ----

    @Size(max = 100, message = "Contact person must be at most 100 characters")
    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Size(max = 150, message = "Contact email must be at most 150 characters")
    @Column(name = "contact_email", length = 150)
    private String contactEmail;

    @Size(max = 200, message = "Job role must be at most 200 characters")
    @Column(name = "job_role", length = 200)
    private String jobRole;

    @DecimalMin(value = "0.0", message = "Package cannot be negative")
    @DecimalMax(value = "1000.0", message = "Package appears unusually high")
    @Column(name = "package_lpa")
    private Double packageLpa;

    @DecimalMin(value = "0.0", message = "Minimum CGPA cannot be negative")
    @DecimalMax(value = "10.0", message = "Minimum CGPA must be at most 10.0")
    @Column(name = "drive_eligibility_cgpa")
    private Double driveEligibilityCgpa;

    @Column(name = "drive_date")
    private LocalDate driveDate;

    @Column(name = "application_deadline")
    private LocalDate applicationDeadline;

    @Size(max = 200, message = "Drive location must be at most 200 characters")
    @Column(name = "drive_location", length = 200)
    private String driveLocation;

    @Size(max = 500, message = "Selection process must be at most 500 characters")
    @Column(name = "selection_process", length = 500)
    private String selectionProcess;

    @Size(max = 1000, message = "Drive description must be at most 1000 characters")
    @Column(name = "drive_description", length = 1000)
    private String driveDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "drive_status", length = 20)
    private DriveStatus driveStatus = DriveStatus.OPEN;

    @Size(max = 500, message = "Eligibility summary must be at most 500 characters")
    @Column(name = "drive_eligibility_summary", length = 500)
    private String driveEligibilitySummary;

    @Size(max = 512, message = "Logo URL must be at most 512 characters")
    @Column(name = "logo_url", length = 512)
    private String logoUrl;

    // ---- Reverse relationships ----

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobOpportunity> jobOpportunities;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlacementDrive> drives;

    /** Simple directory status derived from driveStatus + dates. */
    public enum DriveStatus {
        OPEN, CLOSED, COMPLETED, UPCOMING
    }

    public boolean hasRecruitmentContext() {
        return driveStatus != null || driveDate != null || jobRole != null && !jobRole.isBlank();
    }

    /**
     * Lightweight "equality" helper for selects/forms so React can safely
     * compare company objects when the backend returns different proxy
     * instances for the same id.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(id, company.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

