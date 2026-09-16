package com.placetrack.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mock_interview_answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MockInterviewAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    @JsonIgnore
    private MockInterviewSession session;

    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "question_text", nullable = false, length = 1000)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 40)
    private InterviewQuestion.Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", length = 20)
    private InterviewQuestion.Difficulty difficulty;

    @Column(name = "student_answer", columnDefinition = "TEXT")
    private String studentAnswer;

    @Column(name = "sample_answer", columnDefinition = "TEXT")
    private String sampleAnswer;

    @Column(name = "score")
    private Integer score;

    @Column(name = "is_strong")
    private Boolean isStrong;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt = LocalDateTime.now();
}
