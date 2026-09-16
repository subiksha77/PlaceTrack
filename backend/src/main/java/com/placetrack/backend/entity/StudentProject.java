package com.placetrack.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** A project on a student's placement profile. */
@Entity
@Table(name = "student_projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore
    private Student student;

    @NotBlank(message = "Project name is required")
    @Size(max = 120, message = "Project name must be at most 120 characters")
    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Size(max = 3000, message = "Description must be at most 3000 characters")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Size(max = 300, message = "Technologies must be at most 300 characters")
    @Column(name = "technologies", length = 300)
    private String technologies;

    @Size(max = 300, message = "Project link must be at most 300 characters")
    @Column(name = "project_link", length = 300)
    private String projectLink;

    @Size(max = 60, message = "Project role must be at most 60 characters")
    @Column(name = "project_role", length = 60)
    private String projectRole;
}
