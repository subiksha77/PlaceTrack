package com.placetrack.backend.repository;

import com.placetrack.backend.entity.MockInterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockInterviewSessionRepository extends JpaRepository<MockInterviewSession, Long> {

    List<MockInterviewSession> findByStudentIdOrderByStartedAtDesc(Long studentId);

    long countByStudentId(Long studentId);

    long countByStudentIdAndStatus(Long studentId, MockInterviewSession.Status status);
}
