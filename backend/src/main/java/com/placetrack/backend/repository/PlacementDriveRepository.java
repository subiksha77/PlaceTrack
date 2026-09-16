package com.placetrack.backend.repository;

import com.placetrack.backend.entity.Company;
import com.placetrack.backend.entity.PlacementDrive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlacementDriveRepository extends JpaRepository<PlacementDrive, Long> {

    @Query("SELECT d FROM PlacementDrive d JOIN FETCH d.jobOpportunity JOIN FETCH d.company")
    List<PlacementDrive> findAllWithOpportunityAndCompany();

    @Query("SELECT d FROM PlacementDrive d JOIN FETCH d.jobOpportunity JOIN FETCH d.company WHERE d.company.id = :companyId")
    List<PlacementDrive> findByCompanyIdWithOpportunityAndCompany(Long companyId);

    @Query("SELECT d FROM PlacementDrive d JOIN FETCH d.jobOpportunity JOIN FETCH d.company ORDER BY d.driveDate ASC")
    List<PlacementDrive> findAllOrderByDriveDate();
}