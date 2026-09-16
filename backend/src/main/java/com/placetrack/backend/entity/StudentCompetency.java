package com.placetrack.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "student_competencies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCompetency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /** Example: Communication, Problem Solving, Teamwork, Leadership */
    @NotBlank(message = "Competency is required")
    @Size(min = 2, max = 100, message = "Competency must be 2-100 characters")
    @Column(name = "competency", nullable = false)
    private String competency;

    /** self, basic, intermediate, advanced, expert */
    @NotBlank(message = "Level is required")
    @Size(min = 2, max = 20, message = "Level must be 2-20 characters")
    @Column(name = "level", nullable = false)
    private String level;
}
