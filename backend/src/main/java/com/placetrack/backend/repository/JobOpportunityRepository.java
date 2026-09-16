package com.placetrack.backend.repository;

import com.placetrack.backend.entity.JobOpportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobOpportunityRepository extends JpaRepository<JobOpportunity, Long> {

    List<JobOpportunity> findByCompanyId(Long companyId);

    List<JobOpportunity> findByStatus(JobOpportunity.OpportunityStatus status);

    @Query("SELECT j FROM JobOpportunity j WHERE " +
            "(:status IS NULL OR j.status = :status) AND " +
            "(:search IS NULL OR LOWER(j.jobRole) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(j.company.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(j.workLocation) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(j.requiredSkills) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<JobOpportunity> searchAndFilter(@Param("search") String search,
                                         @Param("status") JobOpportunity.OpportunityStatus status);

    long countByStatus(JobOpportunity.OpportunityStatus status);
}
