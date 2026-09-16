package com.placetrack.backend.service;

import com.placetrack.backend.dto.ApplicationRequest;
import com.placetrack.backend.dto.ApplicationResponse;
import com.placetrack.backend.entity.User;

import java.util.List;

public interface ApplicationService {
    ApplicationResponse apply(User user, ApplicationRequest request);
    List<ApplicationResponse> getMyApplications(User user);
    ApplicationResponse getById(Long id);
    List<ApplicationResponse> getAll(String status, Long companyId, Long studentId);
    ApplicationResponse updateStatus(Long id, String status);
    ApplicationResponse withdraw(User user, Long id);
}
