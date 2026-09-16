package com.placetrack.backend.service;

import com.placetrack.backend.dto.JobOpportunityRequest;
import com.placetrack.backend.dto.JobOpportunityResponse;
import com.placetrack.backend.entity.JobOpportunity;

import java.util.List;

public interface JobOpportunityService {

    JobOpportunityResponse create(JobOpportunityRequest request);

    JobOpportunityResponse update(Long id, JobOpportunityRequest request);

    void delete(Long id);

    List<JobOpportunityResponse> findAll(String search, String status);

    JobOpportunityResponse findById(Long id);

    List<JobOpportunityResponse> findByCompanyId(Long companyId);

    JobOpportunity getEntity(Long id);
}
