package com.placetrack.backend.repository;

import com.placetrack.backend.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Student> findByDepartment(String department);

    List<Student> findByPlacementStatus(Student.PlacementStatus status);

    List<Student> findByDepartmentAndPlacementStatus(String department, Student.PlacementStatus status);

    @Query("SELECT s FROM Student s WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           "LOWER(s.studentName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(s.department) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:department IS NULL OR :department = '' OR LOWER(s.department) = LOWER(:department)) AND " +
           "(:status IS NULL OR :status = '' OR s.placementStatus = :status)")
    List<Student> searchAndFilter(@Param("query") String query,
                                  @Param("department") String department,
                                  @Param("status") Student.PlacementStatus status);

    long countByPlacementStatus(Student.PlacementStatus status);

    @Query("SELECT AVG(s.cgpa) FROM Student s")
    Double findAverageCgpa();

    /** Find the placement profile linked to a login account (users.id). */
    Optional<Student> findByUserId(Long userId);
}
