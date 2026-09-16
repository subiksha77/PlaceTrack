package com.placetrack.backend.service;

import com.placetrack.backend.dto.*;
import com.placetrack.backend.entity.User;

import java.util.List;

public interface MockInterviewService {
    MockInterviewResponse startSession(User user, MockInterviewStartRequest request);
    MockAnswerResponse submitAnswer(User user, Long sessionId, MockAnswerRequest request);
    MockInterviewResponse completeSession(User user, Long sessionId);
    List<MockInterviewResponse> getMySessions(User user);
    MockInterviewResponse getSession(Long sessionId);
}
