package com.placetrack.backend.repository;

import com.placetrack.backend.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query("SELECT c FROM Company c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.jobRole) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Company> searchCompanies(@Param("query") String query);

    List<Company> findByNameContainingIgnoreCase(String name);

    // Drives happening today or scheduled later (not yet completed)
    long countByDriveDateGreaterThanEqual(LocalDate date);

    // Drives scheduled within a date window (e.g. next 30 days)
    long countByDriveDateBetween(LocalDate startInclusive, LocalDate endInclusive);

    // Open drives the average student is eligible for (eligibility CGPA <= maxCgpa)
    @Query("SELECT COUNT(c) FROM Company c WHERE c.driveDate >= :today AND c.driveEligibilityCgpa <= :maxCgpa")
    long countOpenEligible(@Param("today") LocalDate today, @Param("maxCgpa") double maxCgpa);
}
