package com.placetrack.backend.service.impl;

import com.placetrack.backend.entity.Student;
import com.placetrack.backend.exception.DuplicateEmailException;
import com.placetrack.backend.exception.ResourceNotFoundException;
import com.placetrack.backend.repository.StudentRepository;
import com.placetrack.backend.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    public Student createStudent(Student student) {
        // Check for duplicate email
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new DuplicateEmailException(student.getEmail());
        }
        // Default status to NOT_PLACED if null
        if (student.getPlacementStatus() == null) {
            student.setPlacementStatus(Student.PlacementStatus.NOT_PLACED);
        }
        return studentRepository.save(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
    }

    @Override
    public Student updateStudent(Long id, Student updatedStudent) {
        Student existingStudent = getStudentById(id);

        // Check email duplicate only if email changed
        if (!existingStudent.getEmail().equalsIgnoreCase(updatedStudent.getEmail())) {
            if (studentRepository.existsByEmail(updatedStudent.getEmail())) {
                throw new DuplicateEmailException(updatedStudent.getEmail());
            }
        }

        existingStudent.setStudentName(updatedStudent.getStudentName());
        existingStudent.setEmail(updatedStudent.getEmail());
        existingStudent.setDepartment(updatedStudent.getDepartment());
        existingStudent.setYear(updatedStudent.getYear());
        existingStudent.setCgpa(updatedStudent.getCgpa());
        existingStudent.setPhone(updatedStudent.getPhone());
        existingStudent.setSkills(updatedStudent.getSkills());
        existingStudent.setPlacementStatus(updatedStudent.getPlacementStatus());

        return studentRepository.save(existingStudent);
    }

    @Override
    public void deleteStudent(Long id) {
        Student student = getStudentById(id);
        if (student.getPlacements() != null && !student.getPlacements().isEmpty()) {
            throw new com.placetrack.backend.exception.ResourceInUseException(
                    "Student cannot be deleted because placement record(s) exist for this student. Delete the placements first.");
        }
        studentRepository.delete(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> searchAndFilter(String search, String department, String status) {
        boolean hasSearch = StringUtils.hasText(search);
        boolean hasDept = StringUtils.hasText(department);
        boolean hasStatus = StringUtils.hasText(status);

        if (!hasSearch && !hasDept && !hasStatus) {
            return studentRepository.findAll();
        }

        Student.PlacementStatus placementStatus = null;
        if (hasStatus) {
            placementStatus = Student.PlacementStatus.valueOf(status.trim().toUpperCase());
        }

        return studentRepository.searchAndFilter(
                hasSearch ? search.trim() : null,
                hasDept ? department.trim() : null,
                placementStatus
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long countPlaced() {
        return studentRepository.countByPlacementStatus(Student.PlacementStatus.PLACED);
    }

    @Override
    @Transactional(readOnly = true)
    public long countNotPlaced() {
        return studentRepository.countByPlacementStatus(Student.PlacementStatus.NOT_PLACED);
    }
}
