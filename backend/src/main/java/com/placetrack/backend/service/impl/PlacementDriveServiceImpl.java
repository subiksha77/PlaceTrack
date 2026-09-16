package com.placetrack.backend.service.impl;

import com.placetrack.backend.dto.PlacementDriveRequest;
import com.placetrack.backend.dto.PlacementDriveResponse;
import com.placetrack.backend.entity.Company;
import com.placetrack.backend.entity.JobOpportunity;
import com.placetrack.backend.entity.PlacementDrive;
import com.placetrack.backend.exception.ResourceNotFoundException;
import com.placetrack.backend.repository.CompanyRepository;
import com.placetrack.backend.repository.PlacementDriveRepository;
import com.placetrack.backend.service.PlacementDriveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlacementDriveServiceImpl implements PlacementDriveService {

    private final PlacementDriveRepository driveRepository;
    private final CompanyRepository companyRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    @Transactional(readOnly = true)
    public List<PlacementDriveResponse> listAll(Boolean upcoming) {
        List<PlacementDrive> drives;
        if (Boolean.TRUE.equals(upcoming)) {
            drives = driveRepository.findAll().stream()
                    .filter(d -> d.getDriveDate() != null && !d.getDriveDate().isBefore(LocalDate.now()))
                    .toList();
        } else {
            drives = driveRepository.findAll();
        }
        return drives.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PlacementDriveResponse get(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public PlacementDriveResponse create(PlacementDriveRequest request) {
        PlacementDrive drive = fromRequest(new PlacementDrive(), request);
        return toResponse(driveRepository.save(drive));
    }

    @Override
    public PlacementDriveResponse update(Long id, PlacementDriveRequest request) {
        PlacementDrive drive = fromRequest(findOrThrow(id), request);
        return toResponse(driveRepository.save(drive));
    }

    @Override
    public void delete(Long id) {
        driveRepository.delete(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlacementDriveResponse> listByCompany(Long companyId) {
        return driveRepository.findAll().stream()
                .filter(d -> d.getCompany() != null && companyId.equals(d.getCompany().getId()))
                .map(this::toResponse)
                .toList();
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private PlacementDrive findOrThrow(Long id) {
        return driveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlacementDrive", "id", id));
    }

    private PlacementDrive fromRequest(PlacementDrive drive, PlacementDriveRequest req) {
        Company company = companyRepository.findById(req.companyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", req.companyId()));
        drive.setCompany(company);

        // jobOpportunity link is optional at the drive level; skip if not provided
        drive.setTitle(req.jobRole() != null ? req.jobRole() : "Placement Drive");
        drive.setPackageLpa(req.packageLpa());
        drive.setMinCgpa(req.minCgpa());
        drive.setEligibleDepartments(req.eligibleDepartments());
        drive.setRequiredSkills(req.requiredSkills());
        drive.setPreferredSkills(req.preferredSkills());
        drive.setDriveLocation(req.workLocation());
        drive.setSelectionProcess(req.selectionProcess());

        if (req.driveDate() != null && !req.driveDate().isBlank()) {
            drive.setDriveDate(LocalDate.parse(req.driveDate(), DATE_FMT));
        }
        if (req.applicationDeadline() != null && !req.applicationDeadline().isBlank()) {
            drive.setApplicationDeadline(LocalDate.parse(req.applicationDeadline(), DATE_FMT));
        }

        if (req.status() != null && !req.status().isBlank()) {
            try {
                drive.setStatus(PlacementDrive.Status.valueOf(req.status().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
                // keep existing status if value is unrecognised
            }
        }

        if (req.eligibleYear() != null && !req.eligibleYear().isBlank()) {
            try { drive.setEligibleYear(Integer.parseInt(req.eligibleYear())); }
            catch (NumberFormatException ignored) {}
        }
        if (req.graduationYear() != null && !req.graduationYear().isBlank()) {
            try { drive.setGraduationYear(Integer.parseInt(req.graduationYear())); }
            catch (NumberFormatException ignored) {}
        }

        drive.setBacklogsAllowed(req.allowedBacklogs());
        return drive;
    }

    private PlacementDriveResponse toResponse(PlacementDrive d) {
        return new PlacementDriveResponse(
                d.getId(),
                d.getCompany() != null ? d.getCompany().getId() : null,
                d.getCompany() != null ? d.getCompany().getName() : null,
                d.getJobOpportunity() != null ? d.getJobOpportunity().getId() : null,
                d.getTitle(),
                d.getPackageLpa(),
                d.getMinCgpa(),
                d.getEligibleDepartments(),
                d.getEligibleYear() != null ? d.getEligibleYear().toString() : null,
                d.getGraduationYear() != null ? d.getGraduationYear().toString() : null,
                d.getBacklogsAllowed(),
                d.getRequiredSkills(),
                d.getPreferredSkills(),
                d.getDriveDate() != null ? d.getDriveDate().format(DATE_FMT) : null,
                d.getApplicationDeadline() != null ? d.getApplicationDeadline().format(DATE_FMT) : null,
                d.getDriveLocation(),
                d.getSelectionProcess(),
                d.getStatus() != null ? d.getStatus().name() : null
        );
    }
}
