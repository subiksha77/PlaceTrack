package com.placetrack.backend.service;

import com.placetrack.backend.entity.Student;

import java.util.List;

public interface StudentService {

    Student createStudent(Student student);

    List<Student> getAllStudents();

    Student getStudentById(Long id);

    Student updateStudent(Long id, Student student);

    void deleteStudent(Long id);

    List<Student> searchAndFilter(String search, String department, String status);

    long countPlaced();

    long countNotPlaced();
}
