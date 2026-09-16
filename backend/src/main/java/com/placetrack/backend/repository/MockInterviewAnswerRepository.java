package com.placetrack.backend.repository;

import com.placetrack.backend.entity.MockInterviewAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockInterviewAnswerRepository extends JpaRepository<MockInterviewAnswer, Long> {

    List<MockInterviewAnswer> findBySessionIdOrderByIdAsc(Long sessionId);
}
