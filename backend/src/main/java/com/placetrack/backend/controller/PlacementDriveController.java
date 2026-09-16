package com.placetrack.backend.controller;

import com.placetrack.backend.dto.PlacementDriveRequest;
import com.placetrack.backend.dto.PlacementDriveResponse;
import com.placetrack.backend.service.PlacementDriveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/placement-drives", "/api/drives"})
@RequiredArgsConstructor
public class PlacementDriveController {

    private final PlacementDriveService placementDriveService;

    // GET /api/placement-drives?upcoming=true
    @GetMapping
    public ResponseEntity<List<PlacementDriveResponse>> listAll(
            @RequestParam(required = false) Boolean upcoming,
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        return ResponseEntity.ok(placementDriveService.listAll(upcoming));
    }

    // GET /api/placement-drives/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PlacementDriveResponse> get(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        return ResponseEntity.ok(placementDriveService.get(id));
    }

    // POST /api/placement-drives
    @PostMapping
    public ResponseEntity<PlacementDriveResponse> create(
            @RequestBody PlacementDriveRequest request,
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        return new ResponseEntity<>(placementDriveService.create(request), HttpStatus.CREATED);
    }

    // PUT /api/placement-drives/{id}
    @PutMapping("/{id}")
    public ResponseEntity<PlacementDriveResponse> update(
            @PathVariable Long id,
            @RequestBody PlacementDriveRequest request,
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        return ResponseEntity.ok(placementDriveService.update(id, request));
    }

    // DELETE /api/placement-drives/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        placementDriveService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/placement-drives/by-company/{companyId}
    @GetMapping("/by-company/{companyId}")
    public ResponseEntity<List<PlacementDriveResponse>> listByCompany(
            @PathVariable Long companyId,
            @RequestHeader(value = "X-Auth-Token", required = false) String token) {
        return ResponseEntity.ok(placementDriveService.listByCompany(companyId));
    }
}
