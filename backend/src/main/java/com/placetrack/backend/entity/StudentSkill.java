package com.placetrack.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One technical skill on a student's placement profile, grouped by category
 * (programming language, framework, database, tool or other).
 */
@Entity
@Table(name = "student_skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore
    private Student student;

    @NotBlank(message = "Skill name is required")
    @Size(max = 60, message = "Skill name must be at most 60 characters")
    @Column(name = "name", nullable = false, length = 60)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private SkillCategory category = SkillCategory.OTHER;

    public enum SkillCategory {
        PROGRAMMING, FRAMEWORK, DATABASE, TOOL, OTHER
    }
}
