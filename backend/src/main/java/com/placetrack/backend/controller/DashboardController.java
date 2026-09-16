package com.placetrack.backend.controller;

import com.placetrack.backend.service.PlacementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final PlacementService placementService;

    // GET /api/dashboard - Get dashboard statistics from DB
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = placementService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
}
