package com.placetrack.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * One mock-interview attempt by a student.
 *
 * The interview is a real multi-step flow (start -> answer each question ->
 * complete) whose state lives in MySQL, so an in-progress session survives a
 * page refresh or restart. Evaluation is transparent and rule based - see
 * {@link MockInterviewAnswer} for how each answer is scored.
 */
@Entity
@Table(name = "mock_interviews")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MockInterviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Student is required")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /** Optional demo target company name (free text, e.g. "Infosys"). */
    @Column(name = "target_company", length = 150)
    private String targetCompany;

    @Column(name = "target_role", length = 150)
    private String targetRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "interview_type", nullable = false, length = 20)
    private InterviewType interviewType = InterviewType.MIXED;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false, length = 20)
    private Difficulty difficulty = Difficulty.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.IN_PROGRESS;

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions = 0;

    /** Overall percentage (0-100) - null until the session is completed. */
    @Column(name = "score")
    private Integer score;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "strengths", length = 1000)
    private String strengths;

    @Column(name = "improvements", length = 1000)
    private String improvements;

    @Column(name = "suggested_topics", length = 1000)
    private String suggestedTopics;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true,
            fetch = FetchType.LAZY)
    @JsonIgnore
    private List<MockInterviewAnswer> answers = new ArrayList<>();

    public enum InterviewType {
        TECHNICAL, HR, MIXED
    }

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    public enum Status {
        IN_PROGRESS, COMPLETED
    }
}
