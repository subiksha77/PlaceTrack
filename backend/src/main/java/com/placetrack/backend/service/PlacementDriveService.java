package com.placetrack.backend.service;

import com.placetrack.backend.dto.PlacementDriveRequest;
import com.placetrack.backend.dto.PlacementDriveResponse;

import java.util.List;

public interface PlacementDriveService {

    List<PlacementDriveResponse> listAll(Boolean upcoming);

    PlacementDriveResponse get(Long id);

    PlacementDriveResponse create(PlacementDriveRequest request);

    PlacementDriveResponse update(Long id, PlacementDriveRequest request);

    void delete(Long id);

    List<PlacementDriveResponse> listByCompany(Long companyId);
}
