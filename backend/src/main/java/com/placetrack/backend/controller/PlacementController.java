package com.placetrack.backend.controller;

import com.placetrack.backend.entity.Placement;
import com.placetrack.backend.service.PlacementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/placements")
@RequiredArgsConstructor
public class PlacementController {

    private final PlacementService placementService;

    // POST /api/placements - Create a placement
    @PostMapping
    public ResponseEntity<Placement> createPlacement(@Valid @RequestBody Placement placement) {
        Placement created = placementService.createPlacement(placement);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // GET /api/placements - Get all placements (with optional filters)
    @GetMapping
    public ResponseEntity<List<Placement>> getAllPlacements(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long companyId) {

        List<Placement> placements = placementService.filterPlacements(status, companyId);
        return ResponseEntity.ok(placements);
    }

    // GET /api/placements/{id} - Get placement by ID
    @GetMapping("/{id}")
    public ResponseEntity<Placement> getPlacementById(@PathVariable Long id) {
        return ResponseEntity.ok(placementService.getPlacementById(id));
    }

    // PUT /api/placements/{id} - Update placement
    @PutMapping("/{id}")
    public ResponseEntity<Placement> updatePlacement(
            @PathVariable Long id,
            @Valid @RequestBody Placement placement) {
        Placement updated = placementService.updatePlacement(id, placement);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/placements/{id} - Delete placement
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePlacement(@PathVariable Long id) {
        placementService.deletePlacement(id);
        return ResponseEntity.ok(Map.of("message", "Placement deleted successfully"));
    }
}
