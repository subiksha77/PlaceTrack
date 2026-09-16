package com.placetrack.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "student_skills")
@IdClass(StudentSkillId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSkillComposite {

    @Id
    private Long id;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "skill_name", nullable = false)
    private String skillName;
}
