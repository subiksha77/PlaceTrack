package com.placetrack.backend.repository;

import com.placetrack.backend.entity.StudentProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProjectRepository extends JpaRepository<StudentProject, Long> {

    List<StudentProject> findByStudentIdOrderByIdAsc(Long studentId);

    Optional<StudentProject> findByIdAndStudentId(Long id, Long studentId);

    void deleteByStudentId(Long studentId);
}
