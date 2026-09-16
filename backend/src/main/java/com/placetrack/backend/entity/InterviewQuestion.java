package com.placetrack.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One interview-preparation question. Questions are managed by the placement
 * officer (admin) and browsed by students - filtering by category and
 * difficulty, with answer + explanation revealed on demand.
 */
@Entity
@Table(name = "interview_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 40)
    private Category category;

    @NotNull(message = "Difficulty is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false, length = 20)
    private Difficulty difficulty = Difficulty.EASY;

    @NotBlank(message = "Question is required")
    @Size(max = 1000, message = "Question must be at most 1000 characters")
    @Column(name = "question", nullable = false, length = 1000)
    private String question;

    @NotBlank(message = "Answer is required")
    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    /** Free-text topic tag used for search + mock-interview topic suggestions. */
    @Size(max = 100, message = "Topic must be at most 100 characters")
    @Column(name = "topic", length = 100)
    private String topic;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    /** Preparation categories requested by the placement cell. */
    public enum Category {
        APTITUDE("Aptitude"),
        LOGICAL_REASONING("Logical Reasoning"),
        QUANTITATIVE_APTITUDE("Quantitative Aptitude"),
        PROGRAMMING("Programming"),
        JAVA("Java"),
        PYTHON("Python"),
        C_CPP("C/C++"),
        SQL("SQL"),
        DBMS("DBMS"),
        OOP("OOP"),
        DATA_STRUCTURES("Data Structures"),
        ALGORITHMS("Algorithms"),
        OPERATING_SYSTEMS("Operating Systems"),
        COMPUTER_NETWORKS("Computer Networks"),
        WEB_DEVELOPMENT("Web Development"),
        HR("HR");

        private final String label;

        Category(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        /** Lenient parser: accepts enum names ("C_CPP") as well as display labels ("C/C++"). */
        public static Category from(String value) {
            if (value == null || value.isBlank()) {
                return PROGRAMMING;
            }
            String needle = value.trim().toUpperCase().replace(' ', '_').replace("/", "_").replace("+", "P");
            for (Category c : values()) {
                if (c.name().equals(needle) || c.label.toUpperCase().replace(' ', '_').equals(needle)) {
                    return c;
                }
            }
            return PROGRAMMING;
        }
    }

    public enum Difficulty {
        EASY, MEDIUM, HARD;

        public static Difficulty from(String value) {
            if (value == null || value.isBlank()) {
                return EASY;
            }
            try {
                return Difficulty.valueOf(value.trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                return EASY;
            }
        }
    }
}
