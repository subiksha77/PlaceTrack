package com.placetrack.backend.controller;

import com.placetrack.backend.dto.*;
import com.placetrack.backend.entity.User;
import com.placetrack.backend.security.CurrentUserResolver;
import com.placetrack.backend.service.MockInterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mock-interviews")
@RequiredArgsConstructor
public class MockInterviewController {

    private final MockInterviewService mockInterviewService;
    private final CurrentUserResolver currentUserResolver;

    @PostMapping("/start")
    public ResponseEntity<MockInterviewResponse> startSession(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @RequestBody MockInterviewStartRequest request) {
        User user = currentUserResolver.require(token);
        return ResponseEntity.ok(mockInterviewService.startSession(user, request));
    }

    @PostMapping("/{sessionId}/answer")
    public ResponseEntity<MockAnswerResponse> submitAnswer(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @PathVariable Long sessionId,
            @RequestBody MockAnswerRequest request) {
        User user = currentUserResolver.require(token);
        return ResponseEntity.ok(mockInterviewService.submitAnswer(user, sessionId, request));
    }

    @PostMapping("/{sessionId}/complete")
    public ResponseEntity<MockInterviewResponse> completeSession(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @PathVariable Long sessionId) {
        User user = currentUserResolver.require(token);
        return ResponseEntity.ok(mockInterviewService.completeSession(user, sessionId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<MockInterviewResponse>> getMySessions(
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        User user = currentUserResolver.require(token);
        return ResponseEntity.ok(mockInterviewService.getMySessions(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MockInterviewResponse> getSession(@PathVariable Long id) {
        return ResponseEntity.ok(mockInterviewService.getSession(id));
    }
}
