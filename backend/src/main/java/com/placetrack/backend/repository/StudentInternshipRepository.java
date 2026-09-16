package com.placetrack.backend.repository;

import com.placetrack.backend.entity.StudentInternship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentInternshipRepository extends JpaRepository<StudentInternship, Long> {

    List<StudentInternship> findByStudentIdOrderByIdAsc(Long studentId);

    Optional<StudentInternship> findByIdAndStudentId(Long id, Long studentId);

    void deleteByStudentId(Long studentId);
}
