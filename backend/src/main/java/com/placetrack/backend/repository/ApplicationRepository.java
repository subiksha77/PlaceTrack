package com.placetrack.backend.repository;

import com.placetrack.backend.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByStudentIdOrderByAppliedDateDesc(Long studentId);

    List<Application> findByCompanyId(Long companyId);

    List<Application> findByJobOpportunityId(Long jobOpportunityId);

    Optional<Application> findByStudentIdAndJobOpportunityId(Long studentId, Long jobOpportunityId);

    boolean existsByStudentIdAndJobOpportunityId(Long studentId, Long jobOpportunityId);

    long countByStatus(Application.ApplicationStatus status);

    long countByStudentId(Long studentId);

    long countByStudentIdAndStatus(Long studentId, Application.ApplicationStatus status);

    @Query("SELECT a FROM Application a WHERE " +
            "(:status IS NULL OR a.status = :status) AND " +
            "(:companyId IS NULL OR a.company.id = :companyId) AND " +
            "(:studentId IS NULL OR a.student.id = :studentId) " +
            "ORDER BY a.appliedDate DESC")
    List<Application> filterApplications(@Param("status") Application.ApplicationStatus status,
                                         @Param("companyId") Long companyId,
                                         @Param("studentId") Long studentId);
}
