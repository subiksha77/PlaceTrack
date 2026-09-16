package com.placetrack.backend.service;

import com.placetrack.backend.entity.Placement;

import java.util.List;
import java.util.Map;

public interface PlacementService {

    Placement createPlacement(Placement placement);

    List<Placement> getAllPlacements();

    Placement getPlacementById(Long id);

    Placement updatePlacement(Long id, Placement placement);

    void deletePlacement(Long id);

    List<Placement> filterPlacements(String status, Long companyId);

    List<Placement> getRecentPlacements();

    Map<String, Object> getDashboardStats();
}
