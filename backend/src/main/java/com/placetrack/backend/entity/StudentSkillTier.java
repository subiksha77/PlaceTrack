package com.placetrack.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "student_skill_tiers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSkillTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /** The skill this tier applies to (e.g. "Java") */
    @NotBlank(message = "Skill name is required")
    @Size(min = 2, max = 100, message = "Skill name must be 2-100 characters")
    @Column(name = "skill_name", nullable = false)
    private String skillName;

    /** Example: beginner, intermediate, advanced, expert */
    @NotBlank(message = "Tier is required")
    @Size(min = 2, max = 20, message = "Tier must be 2-20 characters")
    @Column(name = "tier", nullable = false)
    private String tier;
}
