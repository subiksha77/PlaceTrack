package com.placetrack.backend.service.impl;

import com.placetrack.backend.dto.JobOpportunityRequest;
import com.placetrack.backend.dto.JobOpportunityResponse;
import com.placetrack.backend.entity.Company;
import com.placetrack.backend.entity.JobOpportunity;
import com.placetrack.backend.exception.ResourceNotFoundException;
import com.placetrack.backend.repository.CompanyRepository;
import com.placetrack.backend.repository.JobOpportunityRepository;
import com.placetrack.backend.service.JobOpportunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobOpportunityServiceImpl implements JobOpportunityService {

    private final JobOpportunityRepository jobRepository;
    private final CompanyRepository companyRepository;

    @Override
    @Transactional
    public JobOpportunityResponse create(JobOpportunityRequest request) {
        Company company = companyRepository.findById(request.companyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", request.companyId()));

        JobOpportunity job = new JobOpportunity();
        mapRequestToEntity(request, job, company);
        JobOpportunity saved = jobRepository.save(job);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public JobOpportunityResponse update(Long id, JobOpportunityRequest request) {
        JobOpportunity job = getEntity(id);
        Company company = companyRepository.findById(request.companyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", "id", request.companyId()));

        mapRequestToEntity(request, job, company);
        JobOpportunity saved = jobRepository.save(job);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        JobOpportunity job = getEntity(id);
        jobRepository.delete(job);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobOpportunityResponse> findAll(String search, String statusStr) {
        JobOpportunity.OpportunityStatus status = null;
        if (statusStr != null && !statusStr.isBlank()) {
            try {
                status = JobOpportunity.OpportunityStatus.valueOf(statusStr.trim().toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }

        String q = (search != null && !search.isBlank()) ? search.trim() : null;
        List<JobOpportunity> list = (q != null || status != null)
                ? jobRepository.searchAndFilter(q, status)
                : jobRepository.findAll();

        return list.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public JobOpportunityResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobOpportunityResponse> findByCompanyId(Long companyId) {
        return jobRepository.findByCompanyId(companyId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public JobOpportunity getEntity(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("JobOpportunity", "id", id));
    }

    private void mapRequestToEntity(JobOpportunityRequest req, JobOpportunity job, Company company) {
        job.setCompany(company);
        job.setJobRole(req.jobRole());
        job.setDescription(req.jobDescription());
        job.setPackageLpa(req.packageLpa());
        if (req.jobType() != null && !req.jobType().isBlank()) {
            try {
                job.setJobType(JobOpportunity.JobType.valueOf(req.jobType().trim().toUpperCase()));
            } catch (Exception e) {
                job.setJobType(JobOpportunity.JobType.FULL_TIME);
            }
        }
        job.setWorkLocation(req.workLocation());
        job.setMinCgpa(req.minCgpa());
        job.setEligibleDepartments(req.eligibleDepartments());
        if (req.eligibleYear() != null && !req.eligibleYear().isBlank()) {
            try { job.setEligibleYear(Integer.parseInt(req.eligibleYear().trim())); } catch (Exception ignored) {}
        }
        if (req.graduationYear() != null && !req.graduationYear().isBlank()) {
            try { job.setGraduationYear(Integer.parseInt(req.graduationYear().trim())); } catch (Exception ignored) {}
        }
        job.setBacklogsAllowed(req.allowedBacklogs());
        job.setRequiredSkills(req.requiredSkills());
        job.setPreferredSkills(req.preferredSkills());
        if (req.driveDate() != null && !req.driveDate().isBlank()) {
            try { job.setDriveDate(LocalDate.parse(req.driveDate().trim())); } catch (Exception ignored) {}
        }
        if (req.applicationDeadline() != null && !req.applicationDeadline().isBlank()) {
            try { job.setApplicationDeadline(LocalDate.parse(req.applicationDeadline().trim())); } catch (Exception ignored) {}
        }
        job.setSelectionProcess(req.selectionProcess());
        if (req.status() != null && !req.status().isBlank()) {
            try {
                job.setStatus(JobOpportunity.OpportunityStatus.valueOf(req.status().trim().toUpperCase()));
            } catch (Exception ignored) {}
        }
    }

    private JobOpportunityResponse toResponse(JobOpportunity job) {
        return new JobOpportunityResponse(
                job.getId(),
                job.getCompany() != null ? job.getCompany().getId() : null,
                job.getCompany() != null ? job.getCompany().getName() : null,
                job.getJobRole(),
                job.getDescription(),
                job.getPackageLpa(),
                job.getJobType() != null ? job.getJobType().name() : null,
                job.getWorkLocation(),
                job.getMinCgpa(),
                job.getEligibleDepartments(),
                job.getEligibleYear() != null ? job.getEligibleYear().toString() : null,
                job.getGraduationYear() != null ? job.getGraduationYear().toString() : null,
                job.getBacklogsAllowed(),
                job.getRequiredSkills(),
                job.getPreferredSkills() != null ? job.getPreferredSkills() : null,
                job.getDriveDate() != null ? job.getDriveDate().toString() : null,
                job.getApplicationDeadline() != null ? job.getApplicationDeadline().toString() : null,
                job.getSelectionProcess(),
                job.getStatus() != null ? job.getStatus().name() : "OPEN",
                Collections.emptyList()
        );
    }
}
