package com.placetrack.backend.controller;

import com.placetrack.backend.dto.ApplicationRequest;
import com.placetrack.backend.dto.ApplicationResponse;
import com.placetrack.backend.entity.User;
import com.placetrack.backend.security.CurrentUserResolver;
import com.placetrack.backend.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;
    private final CurrentUserResolver currentUserResolver;

    @PostMapping
    public ResponseEntity<ApplicationResponse> apply(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @RequestBody ApplicationRequest request) {
        User user = currentUserResolver.require(token);
        return ResponseEntity.ok(applicationService.apply(user, request));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ApplicationResponse>> getMyApplications(
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        User user = currentUserResolver.require(token);
        return ResponseEntity.ok(applicationService.getMyApplications(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) Long studentId) {
        return ResponseEntity.ok(applicationService.getAll(status, companyId, studentId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(applicationService.updateStatus(id, body.get("status")));
    }

    @PutMapping("/{id}/withdraw")
    public ResponseEntity<ApplicationResponse> withdraw(
            @RequestHeader(value = "X-Auth-Token", required = false) String token,
            @PathVariable Long id) {
        User user = currentUserResolver.require(token);
        return ResponseEntity.ok(applicationService.withdraw(user, id));
    }
}
