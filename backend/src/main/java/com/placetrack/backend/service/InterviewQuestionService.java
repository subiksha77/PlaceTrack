package com.placetrack.backend.service;

import com.placetrack.backend.dto.InterviewQuestionRequest;
import com.placetrack.backend.dto.InterviewQuestionResponse;

import java.util.List;
import java.util.Map;

public interface InterviewQuestionService {
    List<InterviewQuestionResponse> getAll(String category, String difficulty, String search);
    InterviewQuestionResponse getById(Long id);
    InterviewQuestionResponse create(InterviewQuestionRequest request);
    InterviewQuestionResponse update(Long id, InterviewQuestionRequest request);
    void delete(Long id);
    List<Map<String, String>> getCategories();
}
