package com.placetrack.backend.service.impl;

import com.placetrack.backend.dto.ApplicationRequest;
import com.placetrack.backend.dto.ApplicationResponse;
import com.placetrack.backend.entity.*;
import com.placetrack.backend.exception.ResourceNotFoundException;
import com.placetrack.backend.exception.UnauthorizedException;
import com.placetrack.backend.repository.ApplicationRepository;
import com.placetrack.backend.repository.JobOpportunityRepository;
import com.placetrack.backend.repository.StudentRepository;
import com.placetrack.backend.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobOpportunityRepository jobOpportunityRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional
    public ApplicationResponse apply(User user, ApplicationRequest request) {
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found. Please complete your profile first."));

        JobOpportunity job = jobOpportunityRepository.findById(request.jobOpportunityId())
                .orElseThrow(() -> new ResourceNotFoundException("Job opportunity not found with id: " + request.jobOpportunityId()));

        if (applicationRepository.existsByStudentIdAndJobOpportunityId(student.getId(), job.getId())) {
            throw new IllegalStateException("You have already applied to this job opportunity.");
        }

        Application application = new Application();
        application.setStudent(student);
        application.setCompany(job.getCompany());
        application.setJobOpportunity(job);
        application.setAppliedDate(LocalDate.now());
        application.setStatus(Application.ApplicationStatus.APPLIED);
        application.setNotes(request.notes());
        application.setStatusUpdatedAt(LocalDateTime.now());

        Application saved = applicationRepository.save(application);
        return toResponse(saved);
    }

    @Override
    public List<ApplicationResponse> getMyApplications(User user) {
        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found."));
        return applicationRepository.findByStudentIdOrderByAppliedDateDesc(student.getId())
                .stream().map(this::toResponse).toList();
    }

    @Override
    public ApplicationResponse getById(Long id) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
        return toResponse(app);
    }

    @Override
    public List<ApplicationResponse> getAll(String status, Long companyId, Long studentId) {
        Application.ApplicationStatus statusEnum = (status != null && !status.isBlank())
                ? Application.ApplicationStatus.from(status) : null;
        return applicationRepository.filterApplications(statusEnum, companyId, studentId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public ApplicationResponse updateStatus(Long id, String status) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
        app.setStatus(Application.ApplicationStatus.from(status));
        app.setStatusUpdatedAt(LocalDateTime.now());
        return toResponse(applicationRepository.save(app));
    }

    @Override
    @Transactional
    public ApplicationResponse withdraw(User user, Long id) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        Student student = studentRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found."));

        if (!app.getStudent().getId().equals(student.getId())) {
            throw new UnauthorizedException("You can only withdraw your own applications.");
        }
        if (app.getStatus().isFinal()) {
            throw new IllegalStateException("Cannot withdraw an application with status: " + app.getStatus());
        }

        app.setStatus(Application.ApplicationStatus.WITHDRAWN);
        app.setStatusUpdatedAt(LocalDateTime.now());
        return toResponse(applicationRepository.save(app));
    }

    private ApplicationResponse toResponse(Application app) {
        return new ApplicationResponse(
                app.getId(),
                app.getStudent().getId(),
                app.getStudent().getStudentName(),
                app.getCompany().getId(),
                app.getCompany().getName(),
                app.getJobOpportunity().getId(),
                app.getJobOpportunity().getJobRole(),
                app.getJobOpportunity().getPackageLpa(),
                app.getAppliedDate() != null ? app.getAppliedDate().toString() : null,
                app.getStatus().name(),
                app.getResumeFileName(),
                app.getNotes(),
                app.getStatusUpdatedAt() != null ? app.getStatusUpdatedAt().toString() : null
        );
    }
}
