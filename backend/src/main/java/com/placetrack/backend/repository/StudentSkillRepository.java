package com.placetrack.backend.repository;

import com.placetrack.backend.entity.StudentSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentSkillRepository extends JpaRepository<StudentSkill, Long> {

    List<StudentSkill> findByStudentIdOrderByIdAsc(Long studentId);

    Optional<StudentSkill> findByIdAndStudentId(Long id, Long studentId);

    void deleteByStudentId(Long studentId);
}
