package com.placetrack.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** An internship experience on a student's placement profile. */
@Entity
@Table(name = "student_internships")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentInternship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnore
    private Student student;

    @NotBlank(message = "Company is required")
    @Size(max = 120, message = "Company must be at most 120 characters")
    @Column(name = "company", nullable = false, length = 120)
    private String company;

    @NotBlank(message = "Role is required")
    @Size(max = 100, message = "Role must be at most 100 characters")
    @Column(name = "role", nullable = false, length = 100)
    private String role;

    @NotBlank(message = "Duration is required")
    @Size(max = 60, message = "Duration must be at most 60 characters")
    @Column(name = "duration", nullable = false, length = 60)
    private String duration;

    @Size(max = 3000, message = "Description must be at most 3000 characters")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
