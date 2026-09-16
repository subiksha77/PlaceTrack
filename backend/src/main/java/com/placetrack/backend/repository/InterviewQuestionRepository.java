package com.placetrack.backend.repository;

import com.placetrack.backend.entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {

    List<InterviewQuestion> findByActiveTrue();

    List<InterviewQuestion> findByCategoryAndActiveTrue(InterviewQuestion.Category category);

    List<InterviewQuestion> findByDifficultyAndActiveTrue(InterviewQuestion.Difficulty difficulty);

    List<InterviewQuestion> findByCategoryAndDifficultyAndActiveTrue(
            InterviewQuestion.Category category, InterviewQuestion.Difficulty difficulty);

    @Query("SELECT q FROM InterviewQuestion q WHERE q.active = true AND " +
            "(:category IS NULL OR q.category = :category) AND " +
            "(:difficulty IS NULL OR q.difficulty = :difficulty) AND " +
            "(:search IS NULL OR LOWER(q.question) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(q.topic) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<InterviewQuestion> searchAndFilter(@Param("category") InterviewQuestion.Category category,
                                            @Param("difficulty") InterviewQuestion.Difficulty difficulty,
                                            @Param("search") String search);
}
