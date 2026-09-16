package com.placetrack.backend.service;

import com.placetrack.backend.dto.EligibilityResultDto;
import com.placetrack.backend.dto.JobRecommendationDto;
import com.placetrack.backend.entity.Student;

import java.util.List;

public interface EligibilityService {

    EligibilityResultDto checkEligibility(Long studentId, Long jobId);

    EligibilityResultDto checkEligibilityForUser(String authToken, Long jobId);

    List<JobRecommendationDto> getRecommendationsForUser(String authToken);

    List<JobRecommendationDto> getRecommendations(Long studentId);
}
