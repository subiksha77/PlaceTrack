package com.placetrack.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

public class StudentSkillId implements Serializable {

    private Long id;
    private Long student;

    public StudentSkillId() {}

    public StudentSkillId(Long id, Long student) {
        this.id = id;
        this.student = student;
    }
}
