package com.placetrack.backend.repository;

import com.placetrack.backend.entity.Placement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlacementRepository extends JpaRepository<Placement, Long> {

    List<Placement> findByStatus(Placement.PlacementStatus status);

    List<Placement> findByCompanyId(Long companyId);

    List<Placement> findByStatusAndCompanyId(Placement.PlacementStatus status, Long companyId);

    List<Placement> findByStudentId(Long studentId);

    List<Placement> findTop10ByOrderByPlacementDateDesc();

    long countByCompanyId(Long companyId);
}
